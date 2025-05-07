package com.hbbhbank.moamoa.payment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {

  PENDING("PENDING", "결제 요청 중"),
  COMPLETED("COMPLETED", "결제 완료"),
  FAILED("FAILED", "결제 실패");

  private final String code;    // 시스템 로직용 결제 상태 코드
  private final String message;
}
