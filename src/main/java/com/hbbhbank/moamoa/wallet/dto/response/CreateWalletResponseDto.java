package com.hbbhbank.moamoa.wallet.dto.response;

import com.hbbhbank.moamoa.wallet.domain.Wallet;

import java.math.BigDecimal;

public record CreateWalletResponseDto(
  Long id,
  String userName,
  String walletNumber,
  String currencyCode,
  String currencyName,
  BigDecimal balance,
  String ExternalBankAccountNumber
) {
  public static CreateWalletResponseDto from(Wallet wallet) {
    return new CreateWalletResponseDto(
      wallet.getId(),
      wallet.getUser().getName(),
      wallet.getWalletNumber(),
      wallet.getCurrency().getCode(),
      wallet.getCurrency().getName(),
      wallet.getBalance(),
      wallet.getAccountLink().getExternalBankAccountNumber()
    );
  }
}