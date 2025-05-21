package com.hbbhbank.moamoa.wallet.dto.response.transaction;

import com.hbbhbank.moamoa.wallet.domain.WalletTransactionStatus;
import com.hbbhbank.moamoa.wallet.domain.WalletTransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDto (
  Long id,
  String walletNumber,
  String counterWalletNumber,
  String currencyCode,
  WalletTransactionType type,
  WalletTransactionStatus status,
  BigDecimal amount,
  LocalDateTime transactedAt,
  boolean external
) {}

