package com.hbbhbank.moamoa.withdraw.service;

import com.hbbhbank.moamoa.external.domain.UserAccountLink;
import com.hbbhbank.moamoa.external.dto.request.transfer.TransferRequestDto;
import com.hbbhbank.moamoa.external.dto.response.transfer.TransferResponseDto;
import com.hbbhbank.moamoa.external.repository.HwanbeeLinkRepository;
import com.hbbhbank.moamoa.external.service.HwanbeeTransferService;
import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.user.service.UserService;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.repository.WalletRepository;
import com.hbbhbank.moamoa.wallet.repository.WalletTransactionRepository;
import com.hbbhbank.moamoa.withdraw.domain.Withdrawal;
import com.hbbhbank.moamoa.withdraw.domain.WithdrawalStatus;
import com.hbbhbank.moamoa.withdraw.dto.request.WithdrawRequestDto;
import com.hbbhbank.moamoa.withdraw.dto.response.WithdrawResponseDto;
import com.hbbhbank.moamoa.withdraw.exception.WithdrawErrorCode;
import com.hbbhbank.moamoa.withdraw.repository.WithdrawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WithdrawService {

  private final WalletRepository walletRepository;
  private final WalletTransactionRepository walletTransactionRepository;
  private final WithdrawRepository withdrawRepository;
  private final HwanbeeTransferService hwanbeeTransferService;
  private final HwanbeeLinkRepository hwanbeeLinkRepository;
  private final UserService userService;

  @Transactional
  public WithdrawResponseDto withdrawToRealAccount(WithdrawRequestDto req) {
    Long userId = userService.getCurrentUserId();

    // 1. 사용자 지갑 조회
    Wallet wallet = walletRepository.findByUserIdAndCurrencyCode(userId, req.currencyCode())
      .orElseThrow(() -> BaseException.type(WithdrawErrorCode.WALLET_NOT_FOUND));

    // 2. 잔액 확인
    if (wallet.getBalance().compareTo(req.amount()) < 0) {
      throw BaseException.type(WithdrawErrorCode.INSUFFICIENT_BALANCE);
    }

    // 3. 연결된 계좌 확인
    UserAccountLink accountLink = hwanbeeLinkRepository.findByUserIdAndCurrencyCode(userId, req.currencyCode())
      .orElseThrow(() -> BaseException.type(WithdrawErrorCode.ACCOUNT_LINK_NOT_FOUND));

    // 4. 외부 송금 실행 (DTO 생성 책임 분리)
    TransferResponseDto responseDto = hwanbeeTransferService.transferFromWalletToLinkedAccount(wallet, accountLink, req.amount());

    // 5. 잔액 차감
    wallet.decreaseBalance(req.amount());

    // 6. 출금 기록 저장
    Withdrawal withdrawal = recordWithdrawal(wallet, accountLink, req.amount(), responseDto.transferredAt());

    return WithdrawResponseDto.from(withdrawal);
  }

  // 출금 기록을 저장한다.
  private Withdrawal recordWithdrawal(Wallet wallet, UserAccountLink accountLink, BigDecimal amount, LocalDateTime time) {
    Withdrawal withdrawal = Withdrawal.builder()
      .userAccountLink(accountLink)
      .wallet(wallet)
      .amount(amount)
      .status(WithdrawalStatus.SUCCESS)
      .withdrawnAt(time)
      .build();

    return withdrawRepository.save(withdrawal);
  }
}
