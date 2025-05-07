package com.hbbhbank.moamoa.recharge.domain;

import com.hbbhbank.moamoa.external.domain.UserAccountLink;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "recharges")
public class Recharge {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "recharge_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "wallet_id", nullable = false)
  private Wallet wallet;

  @Column(name = "recharge_amount", nullable = false)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(name = "recharge_method", nullable = false)
  private RechargeMethod method;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_account_link_id", nullable = false)
  private UserAccountLink accountLink;

  @Column(name = "recharged_at", nullable = false)
  private LocalDateTime rechargedAt;

  @Column(name = "exchange_rate", precision = 18, scale = 8)
  private BigDecimal exchangeRate;

  @Column(name = "fee", precision = 18, scale = 8)
  private BigDecimal fee;

  @Builder
  public Recharge(Wallet wallet, BigDecimal amount, RechargeMethod method,
                  UserAccountLink accountLink, LocalDateTime rechargedAt,
                  BigDecimal exchangeRate, BigDecimal fee) {
    this.wallet = wallet;
    this.amount = amount;
    this.method = method;
    this.accountLink = accountLink;
    this.rechargedAt = rechargedAt;
    this.exchangeRate = exchangeRate;
    this.fee = fee;
  }
}
