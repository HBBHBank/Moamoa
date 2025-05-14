package com.hbbhbank.moamoa.wallet.dto.response;

import com.hbbhbank.moamoa.wallet.domain.Wallet;

import java.math.BigDecimal;

public record CreateWalletResponseDto(
  Long id,
  Long userId,
  String accountNumber,
  String currencyCode,
  String currencyName,
  BigDecimal balance,
  Long accountLinkId
) {
  public static CreateWalletResponseDto from(Wallet wallet) {
    return new CreateWalletResponseDto(
      wallet.getId(),
      wallet.getUser().getId(),
      wallet.getWalletNumber(),
      wallet.getCurrency().getCode(),
      wallet.getCurrency().getName(),
      wallet.getBalance(),
      wallet.getAccountLink().getId()
    );
  }
}