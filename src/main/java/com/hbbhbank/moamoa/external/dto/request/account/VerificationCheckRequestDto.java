package com.hbbhbank.moamoa.external.dto.request.account;

public record VerificationCheckRequestDto(
  String transactionId,
  Integer inputCode
) {
  public static VerificationCheckRequestDto of(VerificationCheckRequestDto dto) {
    return new VerificationCheckRequestDto(
      dto.transactionId(),
      dto.inputCode()
    );
  }
}
