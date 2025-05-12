package com.hbbhbank.moamoa.recharge.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RechargeMethod {
  DIRECT("DIRECT", "직접 충전"),
  AUTO("AUTO", "자동 충전");

  private final String code;
  private final String message;

  public boolean isDirect() {
    return this == DIRECT;
  }

  public boolean isAuto() {
    return this == AUTO;
  }
}