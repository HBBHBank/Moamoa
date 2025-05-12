package com.hbbhbank.moamoa.recharge.dto.response;

import com.hbbhbank.moamoa.recharge.domain.Recharge;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RechargeResponseDto(
  Long walletId,
  BigDecimal amount,
  LocalDateTime chargedAt
) {
  public static RechargeResponseDto from(Recharge rcg) {
    return new RechargeResponseDto(
      rcg.getWallet().getId(),
      rcg.getAmount(),
      rcg.getRechargedAt()
    );
  }
}
