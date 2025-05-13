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
import com.hbbhbank.moamoa.withdraw.domain.Withdrawal;
import com.hbbhbank.moamoa.withdraw.domain.WithdrawalStatus;
import com.hbbhbank.moamoa.withdraw.dto.request.WithdrawRequestDto;
import com.hbbhbank.moamoa.withdraw.dto.response.WithdrawResponseDto;
import com.hbbhbank.moamoa.withdraw.exception.WithdrawErrorCode;
import com.hbbhbank.moamoa.withdraw.repository.WithdrawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WithdrawService {

  private final WalletRepository walletRepository;
  private final WalletTransactionRepository walletTransactionRepository;
  private final WithdrawRepository withdrawRepository;
  private final HwanbeeTransferService hwanbeeTransferService;
  private final HwanbeeLinkRepository hwanbeeLinkRepository;

  @Transactional
  public WithdrawResponseDto withdrawToRealAccount(WithdrawRequestDto req) {
    Long userId = SecurityUtil.getCurrentUserId();

    Wallet wallet = walletRepository.findByUserIdAndCurrencyCode(userId, req.currencyCode())
      .orElseThrow(() -> BaseException.type(WithdrawErrorCode.WALLET_NOT_FOUND));

    if (wallet.getBalance().compareTo(req.amount()) < 0) {
      throw BaseException.type(WithdrawErrorCode.INSUFFICIENT_BALANCE);
    }

    UserAccountLink accountLink = hwanbeeLinkRepository.findByUserIdAndCurrencyCode(userId, req.currencyCode())
      .orElseThrow(() -> BaseException.type(WithdrawErrorCode.ACCOUNT_LINK_NOT_FOUND));

    LocalDateTime now = LocalDateTime.now();

    TransferRequestDto requestDto = new TransferRequestDto(
      userId,
      wallet.getAccountNumber(),
      accountLink.getExternalBankAccountNumber(),
      req.amount(),
      req.currencyCode(),
      now
    );

    TransferResponseDto responseDto;
    try {
      responseDto = hwanbeeTransferService.transfer(requestDto);
    } catch (Exception e) {
      throw BaseException.type(WithdrawErrorCode.TRANSFER_FAILED);
    }

    wallet.subtractBalance(req.amount());

    walletTransactionRepository.save(WalletTransaction.builder()
      .wallet(wallet)
      .type(WalletTransactionType.WITHDRAWAL)
      .amount(req.amount())
      .includedInSettlement(false)
      .transactedAt(responseDto.transferredAt())
      .build());

    Withdrawal withdrawal = withdrawRepository.save(
      Withdrawal.builder()
        .userAccountLink(accountLink)
        .wallet(wallet)
        .amount(req.amount())
        .status(WithdrawalStatus.SUCCESS)
        .withdrawnAt(responseDto.transferredAt())
        .build()
    );

    return WithdrawResponseDto.from(withdrawal);
  }
}
