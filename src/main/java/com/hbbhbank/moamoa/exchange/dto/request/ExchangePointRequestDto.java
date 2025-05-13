package com.hbbhbank.moamoa.exchange.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ExchangePointRequestDto(

  @NotNull(message = "사용자 ID 입력은 필수입니다.")
  Long userId,

  @NotBlank(message = "환전될 통화 코드 입력은 필수입니다.")
  String fromCurrency,

  @NotBlank(message = "환전할 통화 코드 입력은 필수입니다.")
  String toCurrency,

  @NotNull(message = "환전 금액 입력은 필수입니다.")
  @DecimalMin(value = "0.01", message = "환전 금액은 0.01 이상이어야 합니다.")
  BigDecimal amount

) {}
