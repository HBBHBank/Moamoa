package com.hbbhbank.moamoa.external.dto.request;

public record GenerateVerificationCodeRequestDto(
  Long userId,
  String externalBankAccountNumber,
  String currencyCode
) {
  public static GenerateVerificationCodeRequestDto of(Long userId, GenerateVerificationCodeRequestDto origin) {
    return new GenerateVerificationCodeRequestDto(
      userId,
      origin.externalBankAccountNumber(),
      origin.currencyCode()
    );
  }
}
