package com.hbbhbank.moamoa.user.controller;

import com.hbbhbank.moamoa.user.dto.request.ChangeNameRequestDto;
import com.hbbhbank.moamoa.user.dto.request.ChangePasswordRequestDto;
import com.hbbhbank.moamoa.user.dto.request.ChangePhoneRequestDto;
import com.hbbhbank.moamoa.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;

  @PatchMapping("/me/password")
  public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequestDto dto) {
    userService.changePassword(dto);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/me/phone")
  public ResponseEntity<Void> changePhoneNumber(@RequestBody @Valid ChangePhoneRequestDto dto) {
    userService.changePhoneNumber(dto);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/me/name")
  public ResponseEntity<Void> changeUserName(@RequestBody @Valid ChangeNameRequestDto dto) {
    userService.changeUserName(dto);
    return ResponseEntity.ok().build();
  }
}
