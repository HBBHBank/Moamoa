package com.hbbhbank.moamoa.wallet.dto.response.wallet;

import com.hbbhbank.moamoa.wallet.domain.Wallet;

public record GetWalletInfoResponseDto(
  String maskedName,
  String walletNumber,
  String currencyCode
) {
  public static GetWalletInfoResponseDto from(Wallet wallet) {
    String name = wallet.getUser().getName();
    String masked = name.charAt(0) + "○".repeat(name.length() - 1);
    return new GetWalletInfoResponseDto(masked, wallet.getWalletNumber(), wallet.getCurrency().getCode());
  }
}
