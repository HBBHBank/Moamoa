package com.hbbhbank.moamoa.external.dto.request.transfer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferRequestDto(
  Long userId,
  String fromAccountNumber,        // 고객 실계좌 번호
  String toAccountNumber,          // 모아모아 법인 계좌
  BigDecimal amount,               // 송금 금액
  String currency,                 // KRW 등
  LocalDateTime requestedAt        // 요청 시각
) {
  public static TransferRequestDto of(
    Long userId,
    String fromAccountNumber,
    String toAccountNumber,
    BigDecimal amount,
    String currency
  ) {
    return new TransferRequestDto(
      userId,
      fromAccountNumber,
      toAccountNumber,
      amount,
      currency,
      LocalDateTime.now()
    );
  }
}
