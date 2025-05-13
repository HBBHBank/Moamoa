package com.hbbhbank.moamoa.exchange.exception;

import com.hbbhbank.moamoa.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExchangeErrorCode implements ErrorCode {
  NOT_FOUND_FROM_WALLET(HttpStatus.NOT_FOUND, "EXCHANG_001", "환전 될 지갑을 찾을 수 없습니다."),
  NOT_FOUND_TO_WALLET(HttpStatus.NOT_FOUND, "EXCHANG_002", "환전 할 지갑을 찾을 수 없습니다."),
  INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "EXCHANG_003", "환전 금액이 유효하지 않습니다."),
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