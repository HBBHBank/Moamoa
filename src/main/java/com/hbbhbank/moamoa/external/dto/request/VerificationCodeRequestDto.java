package com.hbbhbank.moamoa.external.dto.request;

public record VerificationCodeRequestDto(
  Long userId,
  String currency,
  String accountNumber
) {
}
