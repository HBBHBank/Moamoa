package com.hbbhbank.moamoa.user.service;

import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.global.security.service.JwtTokenService;
import com.hbbhbank.moamoa.user.domain.ERole;
import com.hbbhbank.moamoa.user.domain.ProfileImage;
import com.hbbhbank.moamoa.user.domain.TermsAgreement;
import com.hbbhbank.moamoa.user.domain.User;
import com.hbbhbank.moamoa.user.dto.request.LoginRequestDto;
import com.hbbhbank.moamoa.user.dto.request.SignUpRequestDto;
import com.hbbhbank.moamoa.user.dto.response.LoginResponseDto;
import com.hbbhbank.moamoa.user.dto.response.SignUpResponseDto;
import com.hbbhbank.moamoa.user.exception.UserErrorCode;
import com.hbbhbank.moamoa.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AuthServiceTest {

  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;
  private JwtTokenService jwtTokenService;
  private AuthService authService;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    passwordEncoder = mock(PasswordEncoder.class);
    jwtTokenService = mock(JwtTokenService.class);
    authService = new AuthService(userRepository, passwordEncoder, jwtTokenService);
  }

  @Test
  @DisplayName("회원가입 성공")
  void signUp_success() {
    SignUpRequestDto dto = new SignUpRequestDto("홍길동", "hong@example.com", "01012345678",
      "securePass123", "securePass123", "img_gom_1", true, true, false);

    given(userRepository.existsByEmail(dto.email())).willReturn(false);
    given(userRepository.existsByPhoneNumber(dto.phoneNumber())).willReturn(false);
    given(passwordEncoder.encode(dto.password())).willReturn("encodedPwd");

    SignUpResponseDto response = authService.signUp(dto);

    assertThat(response.email()).isEqualTo("hong@example.com");
    assertThat(response.profileImage()).isEqualTo("img_gom_1");
  }

  @Test
  void signUp_duplicateEmail() {
    SignUpRequestDto dto = new SignUpRequestDto("홍길동", "hong@example.com", "01012345678",
      "securePass123", "securePass123", "img_gom_1", true, true, false);

    given(userRepository.existsByEmail(dto.email())).willReturn(true);

    assertThatThrownBy(() -> authService.signUp(dto))
      .isInstanceOf(BaseException.class)
      .hasMessageContaining(UserErrorCode.DUPLICATE_EMAIL.getMessage());
  }

  @Test
  void signUp_duplicatePhone() {
    SignUpRequestDto dto = new SignUpRequestDto("홍길동", "hong@example.com", "01012345678",
      "securePass123", "securePass123", "img_gom_1", true, true, false);

    given(userRepository.existsByEmail(dto.email())).willReturn(false);
    given(userRepository.existsByPhoneNumber(dto.phoneNumber())).willReturn(true);

    assertThatThrownBy(() -> authService.signUp(dto))
      .isInstanceOf(BaseException.class)
      .hasMessageContaining(UserErrorCode.INVALID_PHONE.getMessage());
  }

  @Test
  void signUp_passwordMismatch() {
    SignUpRequestDto dto = new SignUpRequestDto("홍길동", "hong@example.com", "01012345678",
      "pass1", "pass2", "img_gom_1", true, true, false);

    assertThatThrownBy(() -> authService.signUp(dto))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
  }

  @Test
  void signUp_termsNotAgreed() {
    SignUpRequestDto dto = new SignUpRequestDto("홍길동", "hong@example.com", "01012345678",
      "pass", "pass", "img_gom_1", false, true, false);

    assertThatThrownBy(() -> authService.signUp(dto))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("서비스 이용 약관에 동의해야 가입이 가능합니다.");
  }

  @Test
  void signUp_privacyNotAgreed() {
    SignUpRequestDto dto = new SignUpRequestDto("홍길동", "hong@example.com", "01012345678",
      "pass", "pass", "img_gom_1", true, false, false);

    assertThatThrownBy(() -> authService.signUp(dto))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("개인정보 처리방침에 동의해야 가입이 가능합니다.");
  }

  @Test
  void signUp_invalidProfileImage() {
    SignUpRequestDto dto = new SignUpRequestDto("홍길동", "hong@example.com", "01012345678",
      "pass", "pass", "invalid_image", true, true, false);

    assertThatThrownBy(() -> authService.signUp(dto))
      .isInstanceOf(BaseException.class)
      .hasMessageContaining("존재하지 않는 프로필 이미지입니다.");
  }

  @Test
  void login_success() {
    LoginRequestDto dto = new LoginRequestDto("hong@example.com", "password123");

    User user = User.builder()
      .name("홍길동")
      .email("hong@example.com")
      .phoneNumber("01012345678")
      .password("encodedPassword")
      .profileImage(ProfileImage.from("img_gom_1"))
      .terms(new TermsAgreement(true, true, false))
      .role(ERole.USER)
      .build();
    ReflectionTestUtils.setField(user, "id", 1L);

    given(userRepository.findByEmail(dto.email())).willReturn(Optional.of(user));
    given(passwordEncoder.matches(dto.password(), user.getPassword())).willReturn(true);
    given(jwtTokenService.issueLoginTokens(user))
      .willReturn(new LoginResponseDto(1L, "access-token", "refresh-token", "USER"));

    LoginResponseDto res = authService.login(dto);

    assertThat(res.userId()).isEqualTo(1L);
    assertThat(res.accessToken()).isEqualTo("access-token");
    assertThat(res.refreshToken()).isEqualTo("refresh-token");
    assertThat(res.role()).isEqualTo("USER");
  }

  @Test
  void login_userNotFound() {
    LoginRequestDto dto = new LoginRequestDto("none@example.com", "password");
    given(userRepository.findByEmail(dto.email())).willReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login(dto))
      .isInstanceOf(BaseException.class)
      .hasMessageContaining(UserErrorCode.USER_NOT_FOUND.getMessage());
  }

  @Test
  void login_invalidPassword() {
    LoginRequestDto dto = new LoginRequestDto("hong@example.com", "wrongPass");

    User user = User.builder()
      .email("hong@example.com")
      .password("encodedPassword")
      .role(ERole.USER)
      .build();

    given(userRepository.findByEmail(dto.email())).willReturn(Optional.of(user));
    given(passwordEncoder.matches(dto.password(), user.getPassword())).willReturn(false);

    assertThatThrownBy(() -> authService.login(dto))
      .isInstanceOf(BaseException.class)
      .hasMessageContaining("비밀번호가 일치하지 않습니다");
  }
}
