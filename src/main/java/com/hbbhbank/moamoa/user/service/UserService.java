package com.hbbhbank.moamoa.user.service;

import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.global.security.util.SecurityUtil;
import com.hbbhbank.moamoa.user.domain.User;
import com.hbbhbank.moamoa.user.dto.request.ChangeNameRequestDto;
import com.hbbhbank.moamoa.user.dto.request.ChangePasswordRequestDto;
import com.hbbhbank.moamoa.user.dto.request.ChangePhoneRequestDto;
import com.hbbhbank.moamoa.user.exception.UserErrorCode;
import com.hbbhbank.moamoa.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  // 비밀번호 변경
  @Transactional
  public void changePassword(ChangePasswordRequestDto dto) {
    User user = getCurrentUser();
    user.validatePassword(dto.oldPassword(), passwordEncoder); // 도메인 책임
    user.changePassword(passwordEncoder.encode(dto.newPassword()));
  }

  // 핸드폰 번호 변경
  @Transactional
  public void changePhoneNumber(ChangePhoneRequestDto dto) {
    if (userRepository.existsByPhoneNumber(dto.phoneNumber())) {
      throw BaseException.type(UserErrorCode.INVALID_PHONE);
    }

    User user = getCurrentUser();
    user.changePhoneNumber(dto.phoneNumber());
  }

  // 이름 변경
  @Transactional
  public void changeUserName(ChangeNameRequestDto dto) {
    User user = getCurrentUser();
    user.changeUserName(dto.name());
  }

  // 로그인 된 사용자 ID 조회
  public Long getCurrentUserId() {
    return SecurityUtil.getCurrentUserId();
  }

  // 특정 사용자 ID로 사용자 조회
  public User getByIdOrThrow(Long userId) {
    return userRepository.findById(userId)
      .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
  }

  // 현재 로그인된 사용자 조회
  public User getCurrentUser() {
    Long userId = SecurityUtil.getCurrentUserId();
    return userRepository.findById(userId)
      .orElseThrow(() -> new BaseException(UserErrorCode.USER_NOT_FOUND));
  }

}