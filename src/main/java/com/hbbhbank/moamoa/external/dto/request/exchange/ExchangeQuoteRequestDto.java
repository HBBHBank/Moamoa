package com.hbbhbank.moamoa.external.dto.request.exchange;

public record ExchangeQuoteRequestDto(
  String fromCurrency,
  String toCurrency,
  String amount // API 요구사항에 따라 BigDecimal도 가능
) {}
