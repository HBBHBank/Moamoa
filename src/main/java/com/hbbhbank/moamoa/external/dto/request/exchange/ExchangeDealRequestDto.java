package com.hbbhbank.moamoa.external.dto.request.exchange;

import java.math.BigDecimal;

public record ExchangeDealRequestDto(
  // from은 KRW로 고정임
  String toCurrency,
  BigDecimal amount
) {
}
