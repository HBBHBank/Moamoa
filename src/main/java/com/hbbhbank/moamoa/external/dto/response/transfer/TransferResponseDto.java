package com.hbbhbank.moamoa.external.dto.response.transfer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferResponseDto(
  String status,                     // SUCCESS or FAILED
  String transactionId,             // 외부 송금 거래 식별 및 추적을 위해. ex) hwanbee-tx-20250508-987654
  LocalDateTime transferredAt,      // 거래 완료 시각
  BigDecimal balanceAfter           // 거래 후 잔액
) {
  public boolean isSuccess() {
    return "SUCCESS".equals(status);
  }
}
