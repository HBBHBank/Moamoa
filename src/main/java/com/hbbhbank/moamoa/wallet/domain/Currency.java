package com.hbbhbank.moamoa.wallet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "currencies")
public class Currency {
  @Id
  @Column(name = "currency_code", length = 10)
  private String code; // KRW, USD, JPY

  @Column(name = "currency_name", length = 50)
  private String name; // '대한민국 원화'와 같은 풀네임

  // true: 외화, false: 원화
  // 외화라면, 직접 충전이 불가능함 (환전을 통해서만 충전이 가능. 정책상 그럼.) -> 환전은 환비 API 이용
  // 원화라면, 직접 충전 가능
  @Column(name = "is_foreign", nullable = false)
  private boolean isForeign; // 외화 여부 (→ 수수료 발생 여부)

  @Column(name = "default_auto_charge_unit", nullable = false)
  private Long defaultAutoChargeUnit; // 자동충전 단위 (ex. KRW: 10000, USD: 10 등)

  @Builder
  public Currency(String code, String name, boolean isForeign, Long defaultAutoChargeUnit) {
    this.code = code;
    this.name = name;
    this.isForeign = isForeign;
    this.defaultAutoChargeUnit = defaultAutoChargeUnit;
  }

  // 환비 API에서 환율/수수료 조회하여 사용
}

