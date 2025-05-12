package com.hbbhbank.moamoa.wallet.dto.response;

import com.hbbhbank.moamoa.external.domain.UserAccountLink;
import com.hbbhbank.moamoa.wallet.domain.Currency;
import com.hbbhbank.moamoa.wallet.domain.Wallet;

import java.math.BigDecimal;

public record WalletInquiryResponseDto(
  String accountNumber,
  Currency currency,
  BigDecimal balance,
  UserAccountLink accountLink
) {
  public static WalletInquiryResponseDto from(Wallet w) {
    return new WalletInquiryResponseDto(
      w.getAccountNumber(),
      w.getCurrency(),
      w.getBalance(),
      w.getAccountLink()
    );
  }
}
