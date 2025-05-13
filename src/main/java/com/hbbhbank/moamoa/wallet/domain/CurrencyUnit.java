package com.hbbhbank.moamoa.wallet.domain;

import java.math.BigDecimal;

public enum CurrencyUnit {
  KRW("KRW", BigDecimal.valueOf(10_000)),
  USD("USD", BigDecimal.valueOf(10)),
  EUR("EUR", BigDecimal.valueOf(10)),
  JPY("JPY", BigDecimal.valueOf(1_000)),
  CNY("CNY", BigDecimal.valueOf(50)),
  VND("VND", BigDecimal.valueOf(200_000)),
  INR("INR", BigDecimal.valueOf(800));

  private final String currencyCode;
  private final BigDecimal unitAmount;

  CurrencyUnit(String currencyCode, BigDecimal unitAmount) {
    this.currencyCode = currencyCode;
    this.unitAmount = unitAmount;
  }

  public BigDecimal getUnitAmount() {
    return unitAmount;
  }

  public static CurrencyUnit fromCode(String code) {
    for (CurrencyUnit unit : values()) {
      if (unit.currencyCode.equalsIgnoreCase(code)) {
        return unit;
      }
    }
    throw new IllegalArgumentException("Unsupported currency code: " + code);
  }
}
