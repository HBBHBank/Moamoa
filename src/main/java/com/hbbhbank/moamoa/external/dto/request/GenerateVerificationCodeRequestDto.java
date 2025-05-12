package com.hbbhbank.moamoa.external.dto.request;

public record GenerateVerificationCodeRequestDto(
  Long userId,
  String externalBankAccountNumber,
  String currencyCode
) {
}
