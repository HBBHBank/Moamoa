package com.hbbhbank.moamoa.external.dto.request;

public record VerificationCheckRequestDto(
  Long userId, 
  String externalBankAccountNumber,
  String verificationCode
) {
  public static VerificationCheckRequestDto of(Long userId, CreateVerificationContext ctx) {
    return new VerificationCheckRequestDto(
      userId,
      ctx.externalAccountNumber(),
      ctx.verificationCode()
    );
  }
}
