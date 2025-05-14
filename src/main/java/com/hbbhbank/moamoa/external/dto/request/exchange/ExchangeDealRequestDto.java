package com.hbbhbank.moamoa.external.dto.request.exchange;

public record ExchangeDealRequestDto(
  String quoteId,            // 환율 견적 ID
  String amount,             // 환전할 금액 (문자열로 전달할 수도 있음, 필요시 BigDecimal로 변경 가능)
  String idempotencyKey      // 멱등성 키 (중복 방지용, UUID 권장)
) {}

