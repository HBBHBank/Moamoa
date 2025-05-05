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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final JwtTokenService jwtTokenService;

  @Transactional
  public SignUpResponseDto signUp(SignUpRequestDto dto) {
    dto.validate();

    if (userRepository.existsByEmail(dto.email())) {
      throw BaseException.type(UserErrorCode.DUPLICATE_EMAIL);
    }

    if (userRepository.existsByPhoneNumber(dto.phoneNumber())) {
      throw BaseException.type(UserErrorCode.INVALID_PHONE);
    }

    String encodedPassword = passwordEncoder.encode(dto.password());

    TermsAgreement terms = TermsAgreement.builder()
      .serviceTermsAgreed(Boolean.TRUE.equals(dto.serviceTermsAgreed()))
      .privacyPolicyAgreed(Boolean.TRUE.equals(dto.privacyPolicyAgreed()))
      .marketingAgreed(Boolean.TRUE.equals(dto.marketingAgreed()))
      .build();

    ProfileImage profileImage = ProfileImage.from(dto.profileImage());

    User user = User.builder()
      .name(dto.name())
      .email(dto.email())
      .phoneNumber(dto.phoneNumber())
      .password(encodedPassword)
      .profileImage(profileImage)
      .terms(terms)
      .role(ERole.USER)
      .build();

    userRepository.save(user);

    return SignUpResponseDto.from(user);
  }

  @Transactional
  public LoginResponseDto login(LoginRequestDto loginRequestDto) {
    User user = userRepository.findByEmail(loginRequestDto.email())
      .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

    if (!passwordEncoder.matches(loginRequestDto.password(), user.getPassword())) {
      throw new BaseException(UserErrorCode.INVALID_PASSWORD);
    }

    // Access & Refresh 토큰 생성
    JwtInfo jwtInfo = jwtUtil.generateTokens(user.getId(), user.getRole());

    // RefreshToken 저장
    jwtTokenService.saveRefreshToken(
      user.getId(),
      jwtInfo.refreshToken(),
      jwtInfo.refreshTokenExpirySeconds()
    );

    // 클라이언트에게 보낼 응답 생성
    return new LoginResponseDto(
      user.getId(),
      jwtInfo.accessToken(),
      jwtInfo.refreshToken(),
      user.getRole().name()
    );
  }
}