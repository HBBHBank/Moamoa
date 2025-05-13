package com.hbbhbank.moamoa.withdraw.service;

import com.hbbhbank.moamoa.external.domain.UserAccountLink;
import com.hbbhbank.moamoa.external.dto.request.TransferRequestDto;
import com.hbbhbank.moamoa.external.dto.response.TransferResponseDto;
import com.hbbhbank.moamoa.external.exception.HwanbeeErrorCode;
import com.hbbhbank.moamoa.external.repository.HwanbeeLinkRepository;
import com.hbbhbank.moamoa.external.service.HwanbeeTransferService;
import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.global.security.util.SecurityUtil;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.domain.WalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.WalletTransactionType;
import com.hbbhbank.moamoa.wallet.exception.WalletErrorCode;
import com.hbbhbank.moamoa.wallet.repository.WalletRepository;
import com.hbbhbank.moamoa.wallet.repository.WalletTransactionRepository;
import com.hbbhbank.moamoa.withdraw.dto.request.WithdrawRequestDto;
import com.hbbhbank.moamoa.withdraw.dto.response.WithdrawResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WithdrawService {

  private final WalletRepository walletRepository;
  private final WalletTransactionRepository walletTransactionRepository;
  private final HwanbeeTransferService hwanbeeTransferService;
  private final HwanbeeLinkRepository hwanbeeLinkRepository;

  @Transactional
  public WithdrawResponseDto withdrawToRealAccount(WithdrawRequestDto req) {
    Long userId = SecurityUtil.getCurrentUserId();

    // 1. 유저 지갑 조회
    Wallet wallet = walletRepository.findByUserIdAndCurrencyCode(userId, req.currencyCode())
      .orElseThrow(() -> BaseException.type(WalletErrorCode.NOT_FOUND_WALLET));

    // 2. 잔액 부족 확인
    if (wallet.getBalance().compareTo(req.amount()) < 0) {
      throw BaseException.type(WalletErrorCode.INSUFFICIENT_BALANCE);
    }

    // 3. 사용자-환비 계좌 연결 정보 조회
    UserAccountLink accountLink = hwanbeeLinkRepository.findByUserIdAndCurrencyCode(userId, req.currencyCode())
      .orElseThrow(() -> BaseException.type(HwanbeeErrorCode.INVALID_ACCOUNT_INFO));

    // 4. 환비 API에 출금 요청
    TransferRequestDto requestDto = new TransferRequestDto(
      userId,
      wallet.getAccountNumber(),
      accountLink.getExternalBankAccountNumber(),
      req.amount(),
      req.currencyCode(),
      LocalDateTime.now()
    );

    TransferResponseDto responseDto = hwanbeeTransferService.transfer(requestDto);

    // 5. 잔액 차감
    wallet.subtractBalance(req.amount());

    // 6. 거래 기록 저장
    walletTransactionRepository.save(WalletTransaction.builder()
      .wallet(wallet)
      .type(WalletTransactionType.WITHDRAWAL)
      .amount(req.amount())
      .includedInSettlement(false)
      .transactedAt(responseDto.transferredAt())
      .build());

    // 7. 응답 반환
    return new WithdrawResponseDto(
      req.currencyCode(),
      req.amount(),
      wallet.getBalance(),
      responseDto.transferredAt()
    );
  }
}
