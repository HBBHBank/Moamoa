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

  @Transactional
  public void changePassword(ChangePasswordRequestDto dto) {
    User user = getCurrentUser();

    if (!passwordEncoder.matches(dto.oldPassword(), user.getPassword())) {
      throw new BaseException(UserErrorCode.INVALID_PASSWORD);
    }

    user.changePassword(passwordEncoder.encode(dto.newPassword()));
  }

  @Transactional
  public void changePhoneNumber(ChangePhoneRequestDto dto) {
    if (userRepository.existsByPhoneNumber(dto.phoneNumber())) {
      throw new BaseException(UserErrorCode.INVALID_PHONE);
    }
    User user = getCurrentUser();
    user.changePhoneNumber(dto.phoneNumber());
  }

  @Transactional
  public void changeUserName(ChangeNameRequestDto dto) {
    User user = getCurrentUser();
    user.changeUserName(dto.name());
  }

  private User getCurrentUser() {
    Long userId = SecurityUtil.getCurrentUserId();
    return userRepository.findById(userId)
      .orElseThrow(() -> new BaseException(UserErrorCode.USER_NOT_FOUND));
  }
}