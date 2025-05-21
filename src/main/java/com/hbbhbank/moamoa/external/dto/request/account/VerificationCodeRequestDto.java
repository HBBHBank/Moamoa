package com.hbbhbank.moamoa.external.dto.request.account;

public record VerificationCodeRequestDto(
  String externalBankAccountNumber
) {
  public static VerificationCodeRequestDto of(VerificationCodeRequestDto origin) {
    return new VerificationCodeRequestDto(
      origin.externalBankAccountNumber()
    );
  }
}
