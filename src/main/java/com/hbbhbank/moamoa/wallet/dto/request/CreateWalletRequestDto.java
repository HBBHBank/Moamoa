package com.hbbhbank.moamoa.wallet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateWalletRequestDto(

  @NotBlank(message = "통화 코드는 필수입니다.")
  @Pattern(
    regexp = "^(KRW|USD|JPY|EUR|VND|CNY|INR)$",
    message = "지원하지 않는 통화입니다."
  )
  String currencyCode,

  @NotBlank(message = "외부 계좌번호는 필수입니다.")
  @Pattern(
    regexp = "^HWB\\d{10}$",
    message = "외부 계좌번호 형식이 올바르지 않습니다. (예: HWB1234567890)"
  )
  String externalAccountNumber,

  @NotBlank(message = "인증 코드는 필수입니다.")
  @Pattern(
    regexp = "^[A-Z]{2}\\d{4}$",
    message = "인증 코드 형식이 올바르지 않습니다. (예: FX2031)"
  )
  String verificationCode
) {}

