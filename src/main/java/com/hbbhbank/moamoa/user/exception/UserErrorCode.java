package com.hbbhbank.moamoa.user.exception;

import com.hbbhbank.moamoa.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "존재하지 않는 사용자입니다."),
  DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER_002", "이미 사용 중인 이메일입니다."),
  INVALID_PHONE(HttpStatus.BAD_REQUEST, "USER_003", "유효하지 않은 전화번호입니다."),
  NOT_FOUND_PROFILE_IMAGE(HttpStatus.NOT_FOUND, "USER_004", "존재하지 않는 프로필 이미지입니다."),
  ;

  private final HttpStatus status;
  private final String errorCode;
  private final String message;

  @Override
  public HttpStatus status() { return status; }

  @Override
  public String errorCode() { return errorCode; }

  @Override
  public String message() { return message; }
}


