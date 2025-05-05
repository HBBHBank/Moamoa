package com.hbbhbank.moamoa.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequestDto(

  @Size(min=6, max=20, message = "비밀번호는 6자 이상 20자 이하로 입력해야 합니다.")
  @NotBlank
  String oldPassword,

  @Size(min=6, max=20, message = "비밀번호는 6자 이상 20자 이하로 입력해야 합니다.")
  @NotBlank
  String newPassword
) {
}
