package com.hbbhbank.moamoa.wallet.dto.response;

import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.domain.WalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.WalletTransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletTransactionResponseDto(
  Long id, // 거래 ID
  Long walletId, // 어떤 지갑에 대한 거래인지
  Long counterWalletId, // 누구에게 보내는 거래인지
  WalletTransactionType type, // 입금인지 출금인지
  BigDecimal amount, // 거래 금액
  boolean includedInSettlement, // 정산 그룹에 공유되는 거래인지
  LocalDateTime transactedAt // 거래가 발생한 시간
) {
  public static WalletTransactionResponseDto from(WalletTransaction wt) {
    return new WalletTransactionResponseDto(
      wt.getId(),
      wt.getWallet().getId(),
      wt.getCounterWallet() != null ? wt.getCounterWallet().getId() : null,
      wt.getType(),
      wt.getAmount(),
      wt.isIncludedInSettlement(),
      wt.getTransactedAt()
    );
  }
}
