package com.hbbhbank.moamoa.recharge.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RechargeMethod {

  MANUAL("MANUAL", "사용자 직접 충전"),
  AUTO("AUTO", "자동 충전");

  private final String code;
  private final String message;
}