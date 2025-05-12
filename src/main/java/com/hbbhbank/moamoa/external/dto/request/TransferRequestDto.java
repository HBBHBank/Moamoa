package com.hbbhbank.moamoa.external.dto.request;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record TransferRequestDto(
  Long userId,
  String fromAccountNumber,        // 고객 실계좌 번호
  String toAccountNumber,          // 모아모아 법인 계좌
  BigDecimal amount,               // 송금 금액
  String currency,                 // KRW 등
  ZonedDateTime requestedAt        // 요청 시각
) {}
