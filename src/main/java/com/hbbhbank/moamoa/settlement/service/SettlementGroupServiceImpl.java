package com.hbbhbank.moamoa.settlement.service;

import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.settlement.domain.*;
import com.hbbhbank.moamoa.settlement.dto.request.CreateSettlementGroupRequestDto;
import com.hbbhbank.moamoa.settlement.dto.request.VerifyJoinCodeRequestDto;
import com.hbbhbank.moamoa.settlement.dto.response.CreateSettlementGroupResponseDto;
import com.hbbhbank.moamoa.settlement.dto.response.ReissueJoinCodeResponseDto;
import com.hbbhbank.moamoa.settlement.dto.response.SettlementTransactionResponseDto;
import com.hbbhbank.moamoa.settlement.dto.response.VerifyJoinCodeResponseDto;
import com.hbbhbank.moamoa.settlement.exception.SettlementErrorCode;
import com.hbbhbank.moamoa.settlement.repository.SettlementGroupRepository;
import com.hbbhbank.moamoa.settlement.repository.SettlementSharePeriodRepository;
import com.hbbhbank.moamoa.settlement.repository.SettlementTransactionQueryRepository;
import com.hbbhbank.moamoa.settlement.repository.SettlementTransactionRepository;
import com.hbbhbank.moamoa.transfer.dto.request.PointTransferRequestDto;
import com.hbbhbank.moamoa.user.domain.User;
import com.hbbhbank.moamoa.user.service.UserService;
import com.hbbhbank.moamoa.wallet.domain.InternalWalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.domain.WalletTransactionStatus;
import com.hbbhbank.moamoa.wallet.domain.WalletTransactionType;
import com.hbbhbank.moamoa.wallet.dto.response.transaction.TransactionResponseDto;
import com.hbbhbank.moamoa.wallet.repository.InternalWalletTransactionRepository;
import com.hbbhbank.moamoa.wallet.repository.WalletRepository;
import com.hbbhbank.moamoa.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettlementGroupServiceImpl implements SettlementGroupService {

  private final SettlementGroupRepository groupRepository;
  private final SettlementTransactionRepository settlementTransactionRepository;
  private final UserService userService;
  private final WalletRepository walletRepository;
  private final WalletService walletService;
  private final InternalWalletTransactionRepository internalWalletTransactionRepository;
  private final SettlementTransactionQueryRepository settlementTransactionQueryRepository;
  private final SettlementSharePeriodRepository sharePeriodRepository;


  /**
   * 정산 그룹 생성
   * 그룹 이름 입력, 공유 지갑 선택, 최대 멤버 수 입력
   * 지갑 1개 당 2개 이상의 정산 그룹을 생성할 수 없음.
   * 지갑이 반드시 존재해야함.
   */
  @Override
  @Transactional
  public CreateSettlementGroupResponseDto createGroup(CreateSettlementGroupRequestDto request) {
    // 1. 현재 로그인한 사용자 조회
    User host = userService.getCurrentUser();

    // 2. 지갑 조회
    Wallet wallet = walletRepository.findById(request.walletId())
      .orElseThrow(() -> new BaseException(SettlementErrorCode.GROUP_NOT_FOUND));

    // 3. 해당 지갑이 이미 공유 지갑으로 사용 중인지 확인
    if (groupRepository.existsByReferencedWallet(wallet)) {
      throw new BaseException(SettlementErrorCode.SETTLEMENT_ALREADY_STARTED);
    }

    // 4. 그룹 생성
    SettlementGroup group = SettlementGroup.builder()
      .groupName(request.groupName())
      .joinCode(UUID.randomUUID().toString().substring(0, 8))
      .groupStatus(GroupStatus.INACTIVE)
      .settlementStatus(SettlementStatus.BEFORE)
      .host(host)
      .referencedWallet(wallet)
      .maxMembers(request.maxMembers())
      .build();

    groupRepository.save(group);

    return new CreateSettlementGroupResponseDto(
      group.getId(), group.getGroupName(), group.getJoinCode(), request.maxMembers()
    );
  }

  /**
   * 초대 코드 검증
   */
  @Override
  @Transactional
  public VerifyJoinCodeResponseDto verifyJoinCode(VerifyJoinCodeRequestDto request) {
    // 1. 초대 코드로 그룹 조회
    SettlementGroup group = groupRepository.findByJoinCode(request.joinCode())
      .orElseThrow(() -> new BaseException(SettlementErrorCode.GROUP_NOT_FOUND));

    // 2. 초대 코드 시도 횟수 증가
    group.incrementJoinAttemptCount();

    // 3. 초대 코드 만료 여부 확인
    boolean valid = group.isJoinCodeValid() && group.getJoinAttemptCount() <= 5;

    return new VerifyJoinCodeResponseDto(group.getId(), group.getGroupName(), valid);
  }

  /**
   * 방장 -> 초대 코드 만료 시 재발급
   */
  @Override
  @Transactional
  public ReissueJoinCodeResponseDto reissueJoinCode(Long groupId) {

    // 1. 정산 그룹 조회
    SettlementGroup group = groupRepository.findById(groupId)
      .orElseThrow(() -> new BaseException(SettlementErrorCode.GROUP_NOT_FOUND));

    // 2. 새로운 초대 코드 발급
    String newCode = UUID.randomUUID().toString().substring(0, 8);

    // 3. 초대 코드 만료 시간 설정 (10분 후)
    LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(10);

    // 4. 초대 코드 업데이트
    group.updateJoinCode(newCode, expiredAt);

    return new ReissueJoinCodeResponseDto(group.getId(), newCode, expiredAt);
  }

  /**
   * 정산 시작
   */
  @Override
  @Transactional
  public void startSettlement(Long groupId) {
    // 1. 그룹 조회
    SettlementGroup group = groupRepository.findById(groupId)
      .orElseThrow(() -> new BaseException(SettlementErrorCode.GROUP_NOT_FOUND));

    // 2. 정산 상태가 'BEFORE'가 아닐 경우 예외
    if (group.getSettlementStatus() != SettlementStatus.BEFORE) {
      throw new BaseException(SettlementErrorCode.SETTLEMENT_ALREADY_STARTED);
    }

    // 3. 현재 진행 중인 공유 기간이 있다면 종료 처리
    group.getSharePeriods().stream()
      .filter(p -> !p.isClosed())
      .findFirst()
      .ifPresent(p -> p.stop(LocalDateTime.now()));

    // 4. 정산 상태 변경 및 그룹 비활성화
    group.markSettlementInProgress();
    group.deactivate();
  }


  /**
   * 정산 취소
   * - 정산 그룹이 IN_PROGRESS 상태일 때만 취소 가능
   * - 송금 완료된 정산 내역이 존재하면, 환불 처리 수행
   * - 모든 정산 트랜잭션 삭제
   * - 정산 상태 초기화 및 그룹 재활성화
   * - 거래 공유 종료 시점 초기화
   */
  @Override
  @Transactional
  public void cancelSettlement(Long groupId) {
    // 1. 정산 그룹 조회
    SettlementGroup group = groupRepository.findById(groupId)
      .orElseThrow(() -> new BaseException(SettlementErrorCode.GROUP_NOT_FOUND));

    // 2. 정산 상태 확인 (IN_PROGRESS 상태여야 함)
    if (group.getSettlementStatus() != SettlementStatus.IN_PROGRESS) {
      throw new BaseException(SettlementErrorCode.SETTLEMENT_NOT_IN_PROGRESS);
    }

    // 3. 현재 활성화된 공유 구간이 있다면 종료 처리
    group.getSharePeriods().stream()
      .filter(p -> p.getStoppedAt() == null)
      .findFirst()
      .ifPresent(p -> p.stop(LocalDateTime.now()));

    // 4. 기존 송금 내역 조회
    List<SettlementTransaction> transactions = settlementTransactionRepository.findByGroup(group);

    // 5. 송금 완료된 거래만 환불 처리
    for (SettlementTransaction tx : transactions) {
      if (tx.isTransferred()) {
        Wallet from = tx.getActualTransaction().getWallet();        // 송금자
        Wallet to = tx.getActualTransaction().getCounterWallet();   // 수신자(방장)
        BigDecimal amount = tx.getAmount();

        // 잔액 되돌리기
        to.decreaseBalance(amount);
        from.increaseBalance(amount);

        // 환불 트랜잭션 생성 및 저장 (보낸 것/받은 것 각각)
        InternalWalletTransaction refundSend = InternalWalletTransaction.create(
          to, from, WalletTransactionType.SETTLEMENT_SEND, WalletTransactionStatus.SUCCESS, amount);
        InternalWalletTransaction refundReceive = InternalWalletTransaction.create(
          from, to, WalletTransactionType.SETTLEMENT_RECEIVE, WalletTransactionStatus.SUCCESS, amount);

        internalWalletTransactionRepository.save(refundSend);
        internalWalletTransactionRepository.save(refundReceive);
      }
    }

    // 6. 정산 트랜잭션 전체 삭제
    settlementTransactionRepository.deleteAll(transactions);

    // 7. 상태 초기화 및 그룹 재활성화
    group.markSettlementBefore(); // 상태: BEFORE
    group.activate();             // 그룹: 활성화
  }

  /**
   * 정산 내역 조회
   * - 공유 지갑에서 공유된 거래 내역의 총합을 계산
   * - 참여자 수로 나누어 1인당 정산 금액 계산
   * - 방장을 제외한 멤버별로 정산 응답 DTO 생성
   */
  @Override
  @Transactional(readOnly = true)
  public List<SettlementTransactionResponseDto> getSettlementTransactions(Long groupId) {
    // 1. 정산 그룹 조회
    SettlementGroup group = groupRepository.findById(groupId)
      .orElseThrow(() -> new BaseException(SettlementErrorCode.GROUP_NOT_FOUND));

    // 2. 정산 상태가 COMPLETE인지 확인
    BigDecimal totalAmount = settlementTransactionQueryRepository.sumByGroupSharePeriods(group);

    // 3. 그룹의 멤버 수 조회
    int memberCount = group.getMembers().size();

    // 4. 방장 제외한 멤버 수로 나누기 (정산 금액 계산)
    BigDecimal individualShare = totalAmount.divide(BigDecimal.valueOf(memberCount), RoundingMode.DOWN);

    // 5. 정산 거래 내역 조회
    return group.getMembers().stream()
      .filter(member -> !member.getUser().equals(group.getHost()))
      .map(member -> new SettlementTransactionResponseDto(
        member.getUser().getId(),
        group.getHost().getId(),
        individualShare.longValue(),
        member.isHasTransferred()
      ))
      .toList();
  }

  /**
   * 방장에게 송금하기
   */
  @Override
  @Transactional
  public void transferToHost(Long groupId, PointTransferRequestDto request) {
    // 1. 정산 그룹 조회
    SettlementGroup group = groupRepository.findById(groupId)
      .orElseThrow(() -> new BaseException(SettlementErrorCode.GROUP_NOT_FOUND));

    // 2. 현재 로그인한 사용자 조회 (송금자)
    User fromUser = userService.getCurrentUser();

    // 3. 송금자 지갑 번호로 지갑 조회 및 검증
    Wallet fromWallet = walletService.getWalletByNumberOrThrow(request.fromWalletNumber());

    // 4. 방장의 공유 지갑 조회 (수신자 지갑)
    Wallet toWallet = group.getReferencedWallet();

    // 5. 송금자 잔액 부족 여부 확인
    if (fromWallet.getBalance().compareTo(request.amount()) < 0) {
      throw new BaseException(SettlementErrorCode.INSUFFICIENT_BALANCE);
    }

    // 6. 이미 송금한 사용자라면 예외 처리
    if (settlementTransactionRepository.existsByGroupAndFromUser(group, fromUser)) {
      throw new BaseException(SettlementErrorCode.USER_ALREADY_TRANSFERRED);
    }

    // 7. 송금자 잔액 차감, 수신자 잔액 증가
    fromWallet.decreaseBalance(request.amount());
    toWallet.increaseBalance(request.amount());

    // 8. 내부 거래 트랜잭션 생성 및 저장 (정산 보내기 + 받기 기록 분리 가능)
    InternalWalletTransaction sendTx = InternalWalletTransaction.create(
      fromWallet, toWallet,
      WalletTransactionType.SETTLEMENT_SEND,
      WalletTransactionStatus.SUCCESS,
      request.amount()
    );
    internalWalletTransactionRepository.save(sendTx);

    // 9. 정산 거래 내역 생성 및 상태 업데이트
    SettlementTransaction st = SettlementTransaction.create(group, fromUser, request.amount());
    st.markTransferred(sendTx);
    settlementTransactionRepository.save(st);

    // 10. 정산 멤버의 송금 상태 업데이트
    group.getMembers().stream()
      .filter(m -> m.getUser().equals(fromUser))
      .findFirst()
      .ifPresent(SettlementMember::markTransferred);

    // 11. 모든 멤버가 송금 완료했는지 확인 후, 완료 처리 및 공유 재시작
    boolean allTransferred = group.getMembers().stream().allMatch(SettlementMember::isHasTransferred);
    if (allTransferred) {
      group.markSettlementComplete();        // 정산 완료 상태 변경
      group.activate();                      // 정산 그룹 활성화
      group.restartSharing(LocalDateTime.now()); // 거래 공유 재시작
    }
  }

  /**
   * 그룹 폭파하기
   */
  @Override
  @Transactional
  public void deleteGroup(Long groupId) {
    // 1. 그룹 조회
    SettlementGroup group = groupRepository.findById(groupId)
      .orElseThrow(() -> new BaseException(SettlementErrorCode.GROUP_NOT_FOUND));

    // 2. 정산 상태가 COMPLETE인지 확인 (도메인 명확성)
    if (group.getSettlementStatus() != SettlementStatus.COMPLETE) {
      throw new BaseException(SettlementErrorCode.SETTLEMENT_NOT_COMPLETE);
    }

    // 3. 모든 멤버가 송금 완료했는지 확인
    boolean allSettled = group.getMembers().stream().allMatch(SettlementMember::isHasTransferred);
    if (!allSettled) {
      throw new BaseException(SettlementErrorCode.SETTLEMENT_NOT_COMPLETE);
    }

    // 4. 그룹 삭제 (cascade로 자식까지 함께 삭제됨을 보장)
    groupRepository.delete(group);
  }

  /**
   * 멤버가 그룹 나가기
   * - 정산 완료 전까지는 나갈 수 없음
   */
  @Override
  @Transactional
  public void leaveGroup(Long groupId) {
    // 1. 현재 로그인한 사용자 조회
    User currentUser = userService.getCurrentUser();

    // 2. 정산 그룹 조회
    SettlementGroup group = groupRepository.findById(groupId)
      .orElseThrow(() -> new BaseException(SettlementErrorCode.GROUP_NOT_FOUND));

    // 3. 정산 완료 전이면 나갈 수 없음
    if (group.getSettlementStatus() != SettlementStatus.COMPLETE) {
      throw new BaseException(SettlementErrorCode.SETTLEMENT_NOT_COMPLETE);
    }

    // 4. 현재 유저가 그룹의 멤버인지 확인
    SettlementMember target = group.getMembers().stream()
      .filter(m -> m.getUser().equals(currentUser))
      .findFirst()
      .orElseThrow(() -> new BaseException(SettlementErrorCode.MEMBER_NOT_FOUND));

    // 5. 해당 멤버를 그룹에서 제거
    group.getMembers().remove(target);
  }

  /**
   * 방장의 공유 지갑의 거래 내역 공유
   * - 정산 그룹에 참여한 사용자만 접근 가능
   * - 그룹에 설정된 공유 기간 내 거래 내역만 반환
   */
  @Override
  @Transactional(readOnly = true)
  public List<TransactionResponseDto> getSharedTransactions(Long groupId, Long userId) {
    // 1. 정산 그룹 조회
    SettlementGroup group = groupRepository.findById(groupId)
      .orElseThrow(() -> new BaseException(SettlementErrorCode.GROUP_NOT_FOUND));

    // 2. 사용자가 해당 그룹의 멤버인지 확인
    if (!group.hasMember(userId)) {
      throw new BaseException(SettlementErrorCode.UNAUTHORIZED_ACCESS);
    }

    // 3. 공유 기간 조회
    List<SettlementSharePeriod> periods = sharePeriodRepository.findAllByGroup(group);

    // 4. 방장의 지갑에서 공유 기간 동안 발생한 거래 내역 조회
    List<InternalWalletTransaction> transactions =
      internalWalletTransactionRepository.findByWalletAndPeriods(group.getReferencedWallet(), periods);

    // 5. DTO 변환 후 반환
    return transactions.stream()
      .map(TransactionResponseDto::from)
      .toList();
  }

  /**
   * 정산 그룹 비활성시 방장의 지갑에서 정산 내역 공유 중지
   * 정산 내역에도 미포함
   */
  @Override
  @Transactional
  public void deactivateGroup(Long groupId) {
    // 1. 정산 그룹 조회
    SettlementGroup group = groupRepository.findById(groupId)
      .orElseThrow(() -> new BaseException(SettlementErrorCode.GROUP_NOT_FOUND));

    // 2. 그룹 상태 확인
    if (group.getGroupStatus() == GroupStatus.INACTIVE) {
      throw new BaseException(SettlementErrorCode.ALREADY_INACTIVE);
    }

    // 3. 열려 있는 모든 공유 기간 종료
    group.getSharePeriods().stream()
      .filter(p -> !p.isClosed())
      .forEach(p -> p.stop(LocalDateTime.now()));

    group.deactivate();
  }

  /**
   * 정산 그룹 활성시 방장의 지갑에서 정산 내역 공유 시작
   * 정산 내역에 포함 (중지 했을 때 내역은 공유에서 제외)
   */
  @Override
  @Transactional
  public void activateGroup(Long groupId) {
    // 1. 정산 그룹 조회
    SettlementGroup group = groupRepository.findById(groupId)
      .orElseThrow(() -> new BaseException(SettlementErrorCode.GROUP_NOT_FOUND));

    // 2. 이미 활성화된 그룹이라면 예외
    if (group.getGroupStatus() == GroupStatus.ACTIVE) {
      throw new BaseException(SettlementErrorCode.ALREADY_ACTIVE);
    }

    // 3. 아직 닫히지 않은 공유 기간이 있다면 예외 (중복 공유 방지)
    boolean hasOpenPeriod = group.getSharePeriods().stream()
      .anyMatch(p -> !p.isClosed());

    if (hasOpenPeriod) {
      throw new BaseException(SettlementErrorCode.ACTIVE_SHARE_PERIOD_EXISTS);
    }

    // 4. 그룹 활성화 및 새로운 공유 기간 시작
    group.activate();
    SettlementSharePeriod newPeriod = SettlementSharePeriod.start(group, LocalDateTime.now());
    sharePeriodRepository.save(newPeriod);
  }
}