package com.hbbhbank.moamoa.wallet.dto.response;

import com.hbbhbank.moamoa.wallet.domain.WalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.WalletTransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SearchTransactionResponseDto(

  String userName,

  String walletNumber,

  String counterUserName,

  String counterWalletNumber,

  WalletTransactionType type, // 정산/송금/환전 등

  BigDecimal amount,

  LocalDateTime transactedAt // 거래가 발생한 시간
) {
  public static SearchTransactionResponseDto from(WalletTransaction tx) {
    return new SearchTransactionResponseDto(
      tx.getWallet() != null ? tx.getWallet().getUser().getName() : null,
      tx.getWallet() != null ? tx.getWallet().getWalletNumber() : null,
      tx.getCounterWallet() != null ? tx.getCounterWallet().getUser().getName() : null,
      tx.getCounterWallet() != null ? tx.getCounterWallet().getWalletNumber() : null,
      tx.getType(),
      tx.getAmount(),
      tx.getTransactedAt()
    );
  }
}
