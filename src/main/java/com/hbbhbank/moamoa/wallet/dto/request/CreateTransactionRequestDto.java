package com.hbbhbank.moamoa.wallet.dto.request;

import com.hbbhbank.moamoa.wallet.domain.WalletTransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateTransactionRequestDto(

  @NotNull(message = "지갑 ID는 필수입니다.")
  Long walletId,

  Long counterWalletId,

  @NotNull(message = "거래 유형은 필수입니다.")
  WalletTransactionType type,

  @NotNull(message = "거래 금액은 필수입니다.")
  @DecimalMin(value = "0.0", inclusive = false, message = "거래 금액은 0보다 커야 합니다.")
  BigDecimal amount,

  boolean includedInSettlement

) {}
