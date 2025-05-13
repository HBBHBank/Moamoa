package com.hbbhbank.moamoa.exchange.dto.response;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record ExchangePointResponseDto(
  String fromCurrency,
  String toCurrency,
  BigDecimal fromAmount,
  BigDecimal exchangedAmount,
  BigDecimal rate,
  ZonedDateTime exchangedAt
) {}
