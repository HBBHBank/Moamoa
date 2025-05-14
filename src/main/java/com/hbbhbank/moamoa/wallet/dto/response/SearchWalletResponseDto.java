package com.hbbhbank.moamoa.wallet.dto.response;

import com.hbbhbank.moamoa.wallet.domain.Wallet;

import java.math.BigDecimal;

public record SearchWalletResponseDto(
  String walletNumber,
  String currencyCode,
  String currencyName,
  BigDecimal balance,
  String ExternalBankAccountNumber
) {
  public static SearchWalletResponseDto from(Wallet w) {
    return new SearchWalletResponseDto(
      w.getWalletNumber(),
      w.getCurrency().getCode(),
      w.getCurrency().getName(),
      w.getBalance(),
      w.getAccountLink().getExternalBankAccountNumber()
    );
  }
}

