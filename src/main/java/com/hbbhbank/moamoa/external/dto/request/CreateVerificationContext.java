package com.hbbhbank.moamoa.external.dto.request;

import com.hbbhbank.moamoa.wallet.dto.request.CreateWalletRequestDto;

public record CreateVerificationContext(
  String externalAccountNumber,
  String verificationCode,
  String currencyCode
) {
  public static CreateVerificationContext from(CreateWalletRequestDto dto) {
    return new CreateVerificationContext(
      dto.externalAccountNumber(),
      dto.verificationCode(),
      dto.currencyCode()
    );
  }
}
