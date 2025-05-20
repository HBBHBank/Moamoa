package com.hbbhbank.moamoa.external.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/* 해당 엔티티의 필요성:
   실 계좌 충전 or 환전 요청 등을 환비 API에 위임할 때,
   우리 유저 → 어떤 외부 계좌를 사용할지 알 수 있어야 함.
   환비 API의 계좌 ID는 우리 서비스 DB와 다르므로 매핑 정보가 필요. */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_account_links")
public class UserAccountLink { // 유저와 환비 API 실 계좌 연결 정보
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_account_link_id")
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "external_bank_account_id")
  private String externalBankAccountId; // 환비 API

  @Column(name = "external_bank_account_number", nullable = false)
  private String externalBankAccountNumber; // 환비 API

  @Column(name = "currency_code", nullable = false, length = 10)
  private String currencyCode;

  @Builder
  public UserAccountLink(Long userId, String externalBankAccountId, String externalBankAccountNumber, String currencyCode) {
    this.userId = userId;
    this.externalBankAccountId = externalBankAccountId;
    this.externalBankAccountNumber = externalBankAccountNumber;
    this.currencyCode = currencyCode;
  }

  public static UserAccountLink create(
    Long userId,
    String externalAccountId,
    String externalAccountNumber,
    String currencyCode
  ) {
    return UserAccountLink.builder()
      .userId(userId)
      .externalBankAccountId(externalAccountId)
      .externalBankAccountNumber(externalAccountNumber)
      .currencyCode(currencyCode)
      .build();
  }
}

