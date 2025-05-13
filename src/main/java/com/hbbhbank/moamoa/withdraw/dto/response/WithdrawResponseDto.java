package com.hbbhbank.moamoa.withdraw.dto.response;

import com.hbbhbank.moamoa.withdraw.domain.Withdrawal;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WithdrawResponseDto(
  String currencyCode,
  BigDecimal withdrawnAmount,
  BigDecimal remainingBalance,
  LocalDateTime withdrawalAt
) {
  public static WithdrawResponseDto from(Withdrawal withdrawal) {
    return new WithdrawResponseDto(
      withdrawal.getWallet().getCurrency().getCode(),
      withdrawal.getAmount(),
      withdrawal.getWallet().getBalance(),
      withdrawal.getWithdrawnAt()
    );
  }
}
