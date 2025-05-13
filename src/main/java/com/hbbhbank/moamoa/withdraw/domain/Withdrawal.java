package com.hbbhbank.moamoa.withdraw.domain;

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
@Table(name = "withdrawals")
public class Withdrawal {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_account_link_id", nullable = false)
  private UserAccountLink userAccountLink; // 환비 API와 연결된 계좌 정보

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "wallet_id", nullable = false)
  private Wallet wallet; // 출금할 내 지갑

  @Column(name = "withdrawal_amount", nullable = false)
  private BigDecimal amount; // 결제 금액

  @Enumerated(EnumType.STRING)
  @Column(name = "withdrawl_status", nullable = false)
  private WithdrawalStatus status; // PENDING, SUCCESS, FAILED

  @Column(name = "withdrawal_at", nullable = false)
  private LocalDateTime withdrawnAt; // 출금 완료 시각

  @Builder
  public Withdrawal(UserAccountLink userAccountLink, Wallet wallet, BigDecimal amount, WithdrawalStatus status, LocalDateTime withdrawnAt) {
    this.userAccountLink = userAccountLink;
    this.wallet = wallet;
    this.amount = amount;
    this.status = status;
    this.withdrawnAt = withdrawnAt;
  }
}

