package com.hbbhbank.moamoa.wallet.dto.response;

import com.hbbhbank.moamoa.wallet.domain.Wallet;

import java.math.BigDecimal;

public record WalletResponseDto(
  Long id,
  Long userId,
  String accountNumber,
  String currencyCode,
  String currencyName,
  BigDecimal balance,
  Long accountLinkId
) {
  public static WalletResponseDto from(Wallet wallet) {
    return new WalletResponseDto(
      wallet.getId(),
      wallet.getUser().getId(),
      wallet.getAccountNumber(),
      wallet.getCurrency().getCode(),
      wallet.getCurrency().getName(),
      wallet.getBalance(),
      wallet.getAccountLink().getId()
    );
  }
}