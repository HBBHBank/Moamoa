package com.hbbhbank.moamoa.external.dto.request.account;

public record VerificationCodeRequestDto(
  String externalBankAccountNumber,
  String currencyCode
) {
  public static VerificationCodeRequestDto of(VerificationCodeRequestDto origin) {
    return new VerificationCodeRequestDto(
      origin.externalBankAccountNumber(),
      origin.currencyCode()
    );
  }
}
