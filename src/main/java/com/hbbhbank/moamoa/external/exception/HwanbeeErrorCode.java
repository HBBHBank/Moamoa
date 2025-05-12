package com.hbbhbank.moamoa.external.exception;

import com.hbbhbank.moamoa.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HwanbeeErrorCode implements ErrorCode {
  INVALID_ACCOUNT_INFO(HttpStatus.BAD_REQUEST, "HWANBEE_001", "계좌 정보가 올바르지 않거나 소유자가 일치하지 않습니다."),
  ACCOUNT_LINK_FAILED(HttpStatus.BAD_GATEWAY, "HWANBEE_002", "계좌 연결 중 오류가 발생했습니다."),
  VERIFICATION_CODE_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "HWANBEE_003", "인증 코드 요청에 실패했습니다."),
  VERIFICATION_CODE_CHECK_FAILED(HttpStatus.BAD_REQUEST, "HWANBEE_004", "인증 코드 검증에 실패했습니다."),
  ACCOUNT_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, "HWANBEE_005", "계좌 인증에 실패했습니다."),
  ;

  private final HttpStatus status;
  private final String errorCode;
  private final String message;

  @Override public HttpStatus status() { return status; }
  @Override public String errorCode() { return errorCode; }
  @Override public String message() { return message; }
}


