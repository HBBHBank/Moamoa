package com.hbbhbank.moamoa.withdraw.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WithdrawResponseDto(
  String currencyCode,
  BigDecimal withdrawnAmount,
  BigDecimal remainingBalance,
  LocalDateTime withdrawalAt
) {}
