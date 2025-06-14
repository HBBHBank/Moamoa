package com.hbbhbank.moamoa.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangePhoneRequestDto(
  @Pattern(
    regexp = "^01([0|1|6|7|8|9])\\d{3,4}\\d{4}",
    message = "전화번호 형식에 맞게 입력해야 합니다."
  )
  @NotBlank(message = "전화번호는 필수로 입력해야 합니다.")
  String phoneNumber
) {
}
