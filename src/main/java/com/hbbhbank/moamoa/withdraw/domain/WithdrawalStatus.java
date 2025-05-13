package com.hbbhbank.moamoa.withdraw.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WithdrawalStatus {
  PENDING("PENDING", "출금 대기"),
  SUCCESS("SUCCESS", "출금 성공"),
  FAILED("FAILED", "출금 실패"),
  ;

  private final String code;
  private final String message;
}
