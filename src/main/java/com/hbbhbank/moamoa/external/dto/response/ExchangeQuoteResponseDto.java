package com.hbbhbank.moamoa.external.dto.response;

import java.math.BigDecimal;

public record ExchangeQuoteResponseDto(
  String quoteId,
  String status,
  String fromCurrency,
  String toCurrency,
  BigDecimal rate,
  String ttl // 유효시간 (선택)
) {}
