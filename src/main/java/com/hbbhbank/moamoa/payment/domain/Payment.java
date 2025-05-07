package com.hbbhbank.moamoa.payment.domain;

import com.hbbhbank.moamoa.user.domain.User;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.domain.WalletTransaction;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payments")
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "payment_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="user_id", nullable = false)
  private User user; // 결제를 요청한 사용자. 이후 사용자별 결제 내역 조회, 이력 확인 등에 사용됨.

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "wallet_id", nullable = false)
  private Wallet wallet; // 결제할 때 사용한 지갑. 어떤 통화(KRW, USD 등)의 잔액을 사용했는지 추적.

  @Column(name = "store_name", nullable = false)
  private String storeName; // 결제한 가게명 또는 설명

  @Column(name = "payment_amount", nullable = false)
  private Long amount; // 결제 금액 (포인트 기준)

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_status", nullable = false)
  private PaymentStatus status; // PENDING, COMPLETED, FAILED

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "wallet_transaction_id", unique = true)
  private WalletTransaction walletTransaction; // 해당 거래가 지갑 잔액에서 실제로 차감된 내역을 연결

  @CreationTimestamp
  @Column(name = "payment_requested_at", nullable = false)
  private LocalDateTime requestedAt; // 결제 요청 시각

  @Column(name = "completed_at")
  private LocalDateTime completedAt; // 결제 완료 시각

  @Builder
  public Payment(User user, Wallet wallet, String storeName, Long amount, PaymentStatus status) {
    this.user = user;
    this.wallet = wallet;
    this.storeName = storeName;
    this.amount = amount;
    this.status = status;
  }

}
