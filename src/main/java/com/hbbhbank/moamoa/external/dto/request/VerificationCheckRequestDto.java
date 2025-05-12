package com.hbbhbank.moamoa.external.dto.request;

public record VerificationCheckRequestDto(
  Long userId,
  String accountNumber,
  String verificationCode
) {
}
