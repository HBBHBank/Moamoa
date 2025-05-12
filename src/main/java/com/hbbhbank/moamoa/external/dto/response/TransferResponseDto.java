package com.hbbhbank.moamoa.external.dto.response;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record TransferResponseDto(
  String status,                     // SUCCESS or FAILED
  String transactionId,             // hwanbee-tx-20250508-987654
  ZonedDateTime transferredAt,      // 거래 완료 시각
  BigDecimal balanceAfter           // 거래 후 잔액
) {}
