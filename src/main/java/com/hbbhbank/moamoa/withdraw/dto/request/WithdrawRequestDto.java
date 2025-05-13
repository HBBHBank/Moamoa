package com.hbbhbank.moamoa.withdraw.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record WithdrawRequestDto(

  @NotBlank(message = "통화 코드는 필수입니다.")
  String currencyCode,

  @NotNull(message = "출금 금액은 필수입니다.")
  @DecimalMin(value = "0.01", message = "출금 금액은 0보다 커야 합니다.")
  BigDecimal amount

) {}

