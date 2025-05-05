package com.hbbhbank.moamoa.user.service;

import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.global.security.util.SecurityUtil;
import com.hbbhbank.moamoa.user.domain.User;
import com.hbbhbank.moamoa.user.dto.request.ChangeNameRequestDto;
import com.hbbhbank.moamoa.user.dto.request.ChangePasswordRequestDto;
import com.hbbhbank.moamoa.user.dto.request.ChangePhoneRequestDto;
import com.hbbhbank.moamoa.user.exception.UserErrorCode;
import com.hbbhbank.moamoa.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

class UserServiceTest {

  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;
  private UserService userService;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    passwordEncoder = mock(PasswordEncoder.class);
    userService = new UserService(userRepository, passwordEncoder);
  }

  @Test
  @DisplayName("비밀번호 변경 성공")
  void changePassword_success() {
    try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
      Long userId = 1L;
      User user = mock(User.class);

      ChangePasswordRequestDto dto = new ChangePasswordRequestDto("oldPwd", "newPwd");

      mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
      given(userRepository.findById(userId)).willReturn(Optional.of(user));
      given(passwordEncoder.matches("oldPwd", user.getPassword())).willReturn(true);
      given(passwordEncoder.encode("newPwd")).willReturn("encodedNewPwd");

      userService.changePassword(dto);

      verify(user).changePassword("encodedNewPwd");
    }
  }

  @Test
  @DisplayName("기존 비밀번호 불일치 시 예외")
  void changePassword_fail_wrongOldPassword() {
    try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
      Long userId = 1L;
      User user = mock(User.class);

      ChangePasswordRequestDto dto = new ChangePasswordRequestDto("wrongOldPwd", "newPwd");

      mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
      given(userRepository.findById(userId)).willReturn(Optional.of(user));
      given(passwordEncoder.matches("wrongOldPwd", user.getPassword())).willReturn(false);

      assertThatThrownBy(() -> userService.changePassword(dto))
        .isInstanceOf(BaseException.class)
        .hasMessageContaining(UserErrorCode.INVALID_PASSWORD.message());
    }
  }

  @Test
  @DisplayName("전화번호 변경 성공")
  void changePhoneNumber_success() {
    try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
      Long userId = 1L;
      User user = mock(User.class);

      ChangePhoneRequestDto dto = new ChangePhoneRequestDto("01099998888");

      mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
      given(userRepository.findById(userId)).willReturn(Optional.of(user));
      given(userRepository.existsByPhoneNumber("01099998888")).willReturn(false);

      userService.changePhoneNumber(dto);

      verify(user).changePhoneNumber("01099998888");
    }
  }

  @Test
  @DisplayName("이미 등록된 전화번호로 변경 시 예외")
  void changePhoneNumber_fail_alreadyExists() {
    try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
      Long userId = 1L;
      User user = mock(User.class);

      ChangePhoneRequestDto dto = new ChangePhoneRequestDto("01012345678");

      mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
      given(userRepository.existsByPhoneNumber("01012345678")).willReturn(true);

      assertThatThrownBy(() -> userService.changePhoneNumber(dto))
        .isInstanceOf(BaseException.class)
        .hasMessageContaining(UserErrorCode.INVALID_PHONE.message());
    }
  }

  @Test
  @DisplayName("이름 변경 성공")
  void changeUserName_success() {
    try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
      Long userId = 1L;
      User user = mock(User.class);

      ChangeNameRequestDto dto = new ChangeNameRequestDto("이순신");

      mockedSecurityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
      given(userRepository.findById(userId)).willReturn(Optional.of(user));

      userService.changeUserName(dto);

      verify(user).changeUserName("이순신");
    }
  }
}