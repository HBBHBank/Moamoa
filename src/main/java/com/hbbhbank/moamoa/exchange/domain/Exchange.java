package com.hbbhbank.moamoa.exchange.domain;

import com.hbbhbank.moamoa.user.domain.User;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "exchanges")
public class Exchange {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "exchange_transaction_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "from_wallet_id")
  private Wallet fromWallet;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "to_wallet_id")
  private Wallet toWallet;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal fromAmount; // 환전 전 빠지는 금액

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal toAmount;   // 환전 후 들어가는 금액

  @Column(nullable = false, precision = 19, scale = 6)
  private BigDecimal exchangeRate; // 적용된 환율 (예: 11.30)

  @Column(nullable = false)
  private LocalDateTime exchangedAt; // 환전한 시각

  @Builder
  public Exchange(User user,
                             Wallet fromWallet,
                             Wallet toWallet,
                             BigDecimal fromAmount,
                             BigDecimal toAmount,
                             BigDecimal exchangeRate,

                             LocalDateTime exchangedAt) {
    this.user = user;
    this.fromWallet = fromWallet;
    this.toWallet = toWallet;
    this.fromAmount = fromAmount;
    this.toAmount = toAmount;
    this.exchangeRate = exchangeRate;
    this.exchangedAt = exchangedAt;
  }
}

