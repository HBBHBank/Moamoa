package com.hbbhbank.moamoa.external.dto.request.account;

public record VerificationCheckRequestDto(
  Long userId, 
  String externalBankAccountNumber,
  String verificationCode
) {
  public static VerificationCheckRequestDto of(Long userId, AccountVerificationContext ctx) {
    return new VerificationCheckRequestDto(
      userId,
      ctx.externalAccountNumber(),
      ctx.verificationCode()
    );
  }
}
