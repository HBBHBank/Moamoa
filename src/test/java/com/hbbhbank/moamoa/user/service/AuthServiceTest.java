package com.hbbhbank.moamoa.user.service;

import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.user.dto.request.SignUpRequestDto;
import com.hbbhbank.moamoa.user.dto.response.SignUpResponseDto;
import com.hbbhbank.moamoa.user.exception.UserErrorCode;
import com.hbbhbank.moamoa.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

class AuthServiceTest {

  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;
  private AuthService authService;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    passwordEncoder = mock(PasswordEncoder.class);
    authService = new AuthService(userRepository, passwordEncoder);
  }

  @Test
  @DisplayName("회원가입이 정상적으로 완료된다")
  void signUp_success() {
    // given
    SignUpRequestDto dto = new SignUpRequestDto(
      "홍길동",
      "hong@example.com",
      "01012345678",
      "securePass123",
      "securePass123",
      "img_gom_1",
      true,
      true,
      false
    );

    given(userRepository.existsByEmail("hong@example.com")).willReturn(false);
    given(userRepository.existsByPhoneNumber("01012345678")).willReturn(false);
    given(passwordEncoder.encode("securePass123")).willReturn("encodedPwd");

    // when
    SignUpResponseDto response = authService.signUp(dto);

    // then
    assertThat(response).isNotNull();
    assertThat(response.email()).isEqualTo("hong@example.com");
    assertThat(response.profileImage()).isEqualTo("img_gom_1");
  }

  @Test
  @DisplayName("중복 이메일로 회원가입 시 예외 발생")
  void signUp_duplicateEmail() {
    // given
    SignUpRequestDto dto = new SignUpRequestDto(
      "홍길동",
      "hong@example.com",
      "01012345678",
      "securePass123",
      "securePass123",
      "img_gom_1",
      true,
      true,
      false
    );

    given(userRepository.existsByEmail("hong@example.com")).willReturn(true);

    // expect
    assertThatThrownBy(() -> authService.signUp(dto))
      .isInstanceOf(BaseException.class)
      .hasMessageContaining(UserErrorCode.DUPLICATE_EMAIL.getMessage());
  }

  @Test
  @DisplayName("중복 전화번호로 회원가입 시 예외 발생")
  void signUp_duplicatePhoneNumber() {
    // given
    SignUpRequestDto dto = new SignUpRequestDto(
      "홍길동",
      "hong@example.com",
      "01012345678",
      "securePass123",
      "securePass123",
      "img_gom_1",
      true,
      true,
      false
    );

    given(userRepository.existsByEmail("hong@example.com")).willReturn(false);
    given(userRepository.existsByPhoneNumber("01012345678")).willReturn(true);

    // expect
    assertThatThrownBy(() -> authService.signUp(dto))
      .isInstanceOf(BaseException.class)
      .hasMessageContaining(UserErrorCode.INVALID_PHONE.getMessage());
  }

  @Test
  @DisplayName("비밀번호 불일치 시 예외 발생")
  void signUp_passwordMismatch() {
    // given
    SignUpRequestDto dto = new SignUpRequestDto(
      "홍길동",
      "hong@example.com",
      "01012345678",
      "securePass123",
      "securePass1234", // 불일치
      "img_gom_1",
      true,
      true,
      false
    );

    // expect
    assertThatThrownBy(() -> authService.signUp(dto))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("비밀번호와 비밀번호 확인이 일치하지 않습니다");
  }

  @Test
  @DisplayName("약관 동의 안했을 때 예외 발생")
  void signUp_termsNotAgreed() {
    // given
    SignUpRequestDto dto = new SignUpRequestDto(
      "홍길동",
      "hong@example.com",
      "01012345678",
      "securePass123",
      "securePass123",
      "img_gom_1",
      false, // 동의 안함
      true,
      false
    );

    // expect
    assertThatThrownBy(() -> authService.signUp(dto))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("서비스 이용 약관에 동의해야 가입이 가능합니다.");
  }

  @Test
  @DisplayName("개인정보처리방침 동의 안했을 때 예외 발생")
  void signUp_privacyNotAgreed() {
    // given
    SignUpRequestDto dto = new SignUpRequestDto(
      "홍길동",
      "hong@example.com",
      "01012345678",
      "securePass123",
      "securePass123",
      "img_gom_1",
      true,
      false, // 동의 안함
      false
    );

    // expect
    assertThatThrownBy(() -> authService.signUp(dto))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("개인정보 처리방침에 동의해야 가입이 가능합니다.");
  }

  @Test
  @DisplayName("존재하지 않는 프로필 이미지 값 전달 시 예외 발생")
  void signUp_invalidProfileImage() {
    // given
    SignUpRequestDto dto = new SignUpRequestDto(
      "홍길동",
      "hong@example.com",
      "01012345678",
      "securePass123",
      "securePass123",
      "invalid_value",
      true,
      true,
      false
    );

    // expect
    assertThatThrownBy(() -> authService.signUp(dto))
      .isInstanceOf(BaseException.class)
      .hasMessageContaining("존재하지 않는 프로필 이미지입니다.");
  }
}
