package com.hbbhbank.moamoa.external.dto.response;

import java.time.LocalDateTime;

public record VerificationCodeResponseDto(
  String verificationCode,
  LocalDateTime expiredAt
) {
}
