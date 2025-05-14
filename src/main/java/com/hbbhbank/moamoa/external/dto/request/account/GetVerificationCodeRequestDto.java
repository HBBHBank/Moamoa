package com.hbbhbank.moamoa.external.dto.request.account;

import com.hbbhbank.moamoa.wallet.dto.request.GetVerificationCodeWithinMoamoaRequestDto;

public record GetVerificationCodeRequestDto(
  Long userId,
  String externalBankAccountNumber
) {
  public static GetVerificationCodeRequestDto of(Long userId, GetVerificationCodeWithinMoamoaRequestDto origin) {
    return new GetVerificationCodeRequestDto(
      userId,
      origin.externalBankAccountNumber()
    );
  }
}
