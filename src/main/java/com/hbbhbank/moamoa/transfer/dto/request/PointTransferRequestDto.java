package com.hbbhbank.moamoa.transfer.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PointTransferRequestDto(

  @NotNull(message = "보내는 지갑 ID는 필수입니다.")
  Long fromWalletId,

  @NotNull(message = "받는 지갑 ID는 필수입니다.")
  Long toWalletId,

  @NotNull(message = "송금 금액은 필수입니다.")
  @Positive(message = "송금 금액은 0보다 커야 합니다.")
  BigDecimal amount

) {}
