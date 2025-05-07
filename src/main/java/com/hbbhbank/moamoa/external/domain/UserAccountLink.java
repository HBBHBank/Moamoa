package com.hbbhbank.moamoa.external.domain;

import com.hbbhbank.moamoa.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/* 해당 엔티티의 필요성:
   실 계좌 충전 or 환전 요청 등을 환비 API에 위임할 때,
   우리 유저 → 어떤 외부 계좌를 사용할지 알 수 있어야 함.
   환비 API의 계좌 ID는 우리 서비스 DB와 다르므로 매핑 정보가 필요.
   또한, 자동 충전처럼 우선순위 계좌가 필요할 수도 있음. */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_account_links")
public class UserAccountLink { // 유저와 환비 API 실 계좌 연결 정보
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_account_link_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "external_bank_account_id", nullable = false)
  private String externalBankAccountId; // 환비 API

  @Column(name = "is_primary", nullable = false)
  private boolean isPrimary; // 자동 충전 시 사용될 우선 계좌

  @Builder
  public UserAccountLink(User user, String externalBankAccountId, boolean isPrimary) {
    this.user = user;
    this.externalBankAccountId = externalBankAccountId;
    this.isPrimary = isPrimary;
  }
}

