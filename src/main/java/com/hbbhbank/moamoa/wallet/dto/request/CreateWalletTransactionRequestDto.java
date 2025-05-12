package com.hbbhbank.moamoa.wallet.dto.request;

import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.domain.WalletTransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateWalletTransactionRequestDto(

  @NotNull(message = "지갑 정보는 필수입니다.")
  Wallet wallet,

  Wallet counterWallet, // 출금일 경우 서비스 단에서 null 체크

  @NotNull(message = "거래 유형은 필수입니다.")
  WalletTransactionType type,

  @NotNull(message = "거래 금액은 필수입니다.")
  @DecimalMin(value = "0.0", inclusive = false, message = "거래 금액은 0보다 커야 합니다.")
  BigDecimal amount,

  boolean includedInSettlement

) {}
