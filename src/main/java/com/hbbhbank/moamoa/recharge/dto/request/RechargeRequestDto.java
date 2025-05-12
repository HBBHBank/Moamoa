package com.hbbhbank.moamoa.recharge.dto.request;

import java.math.BigDecimal;

public record RechargeRequestDto(
  Long walletId,
  String currencyCode,
  BigDecimal amount
) {}
