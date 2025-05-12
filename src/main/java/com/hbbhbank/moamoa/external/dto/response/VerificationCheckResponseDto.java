package com.hbbhbank.moamoa.external.dto.response;

import java.time.LocalDateTime;

public record VerificationCheckResponseDto(
  String result,
  String externalBankAccountId,
  LocalDateTime verifiedAt
) {
}
