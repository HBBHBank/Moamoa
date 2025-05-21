package com.hbbhbank.moamoa.wallet.dto.response.wallet;

import com.hbbhbank.moamoa.wallet.domain.Wallet;

import java.math.BigDecimal;

public record SearchWalletResponseDto(
  String currencyCode,
  String currencyName,
  BigDecimal balance
) {
  public static SearchWalletResponseDto from(Wallet w) {
    return new SearchWalletResponseDto(
      w.getCurrency().getCode(),
      w.getCurrency().getName(),
      w.getBalance()
    );
  }
}

