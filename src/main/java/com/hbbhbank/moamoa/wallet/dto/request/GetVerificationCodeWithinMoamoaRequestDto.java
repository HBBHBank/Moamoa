package com.hbbhbank.moamoa.wallet.dto.request;

public record GetVerificationCodeWithinMoamoaRequestDto(
  String externalBankAccountNumber,

  String currencyCode
) {
}
