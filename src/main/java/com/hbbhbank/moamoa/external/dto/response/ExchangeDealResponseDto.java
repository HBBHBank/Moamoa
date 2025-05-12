package com.hbbhbank.moamoa.external.dto.response;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record ExchangeDealResponseDto(
  String status,             // SUCCESS 또는 FAILED
  String tradeId,            // 체결 ID
  String fromCurrency,       // 원래 통화
  String toCurrency,         // 바뀐 통화
  BigDecimal executedRate,   // 체결된 환율
  BigDecimal exchangedAmount,// 환전 후 수취 금액
  ZonedDateTime dealtAt      // 체결 시각
) {}
