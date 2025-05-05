package com.hbbhbank.moamoa.user.service;

import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.global.security.info.JwtInfo;
import com.hbbhbank.moamoa.global.security.service.JwtTokenService;
import com.hbbhbank.moamoa.global.security.util.JwtUtil;
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
import static org.mockito.BDDMockito.mock;

class AuthServiceTest {

  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;
  private AuthService authService;
  private JwtUtil jwtUtil;
  private JwtTokenService jwtTokenService;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    passwordEncoder = mock(PasswordEncoder.class);
    jwtUtil = mock(JwtUtil.class);
    jwtTokenService = mock(JwtTokenService.class);
    authService = new AuthService(userRepository, passwordEncoder, jwtUtil, jwtTokenService);
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


  @Test
  @DisplayName("로그인이 성공적으로 이루어진다")
  void login_success() {
    // given
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

    // ID 수동 주입
    ReflectionTestUtils.setField(user, "id", 1L);

    given(userRepository.findByEmail(dto.email())).willReturn(Optional.of(user));
    given(passwordEncoder.matches(dto.password(), user.getPassword())).willReturn(true);
    given(jwtUtil.generateTokens(1L, user.getRole()))
      .willReturn(new JwtInfo("access-token", "refresh-token", 3600L));

    // when
    LoginResponseDto response = authService.login(dto);

    // then
    assertThat(response).isNotNull();
    assertThat(response.userId()).isEqualTo(1L);
    assertThat(response.accessToken()).isEqualTo("access-token");
    assertThat(response.refreshToken()).isEqualTo("refresh-token");
    assertThat(response.role()).isEqualTo("USER");
  }

  @Test
  @DisplayName("존재하지 않는 이메일로 로그인 시 예외 발생")
  void login_userNotFound() {
    // given
    LoginRequestDto dto = new LoginRequestDto("unknown@example.com", "password123");
    given(userRepository.findByEmail(dto.email())).willReturn(Optional.empty());

    // expect
    assertThatThrownBy(() -> authService.login(dto))
      .isInstanceOf(BaseException.class)
      .hasMessageContaining(UserErrorCode.USER_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("비밀번호 불일치 시 로그인 실패")
  void login_invalidPassword() {
    // given
    LoginRequestDto dto = new LoginRequestDto("hong@example.com", "wrongPass");

    User user = User.builder()
      .email("hong@example.com")
      .password("encodedPassword")
      .role(ERole.USER)
      .build();

    given(userRepository.findByEmail(dto.email())).willReturn(Optional.of(user));
    given(passwordEncoder.matches(dto.password(), user.getPassword())).willReturn(false);

    // expect
    assertThatThrownBy(() -> authService.login(dto))
      .isInstanceOf(BaseException.class)
      .hasMessageContaining("비밀번호가 일치하지 않습니다");
  }
}
