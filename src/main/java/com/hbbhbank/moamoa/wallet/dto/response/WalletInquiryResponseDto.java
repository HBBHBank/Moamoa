package com.hbbhbank.moamoa.wallet.dto.response;

import com.hbbhbank.moamoa.wallet.domain.Wallet;

import java.math.BigDecimal;

public record WalletInquiryResponseDto(
  String accountNumber,
  String currencyCode,
  String currencyName,
  BigDecimal balance,
  Long accountLinkId
) {
  public static WalletInquiryResponseDto from(Wallet w) {
    return new WalletInquiryResponseDto(
      w.getWalletNumber(),
      w.getCurrency().getCode(),
      w.getCurrency().getName(),
      w.getBalance(),
      w.getAccountLink().getId()
    );
  }
}

