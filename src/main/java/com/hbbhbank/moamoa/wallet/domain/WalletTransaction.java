package com.hbbhbank.moamoa.wallet.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "wallet_transactions")
public class WalletTransaction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "wallet_transaction_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "wallet_id", nullable = false)
  private Wallet wallet;

  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_type", nullable = false)
  private WalletTransactionType type; // PAYMENT, AUTO_CHARGE, MANUAL_CHARGE, SETTLEMENT_SEND, SETTLEMENT_RECEIVE

  @Column(name = "transaction_amount", nullable = false)
  private Long amount; // 양수: 입금, 음수: 출금

  @Column(name = "description", length = 255)
  private String description;

  @Column(name = "included_in_settlement", nullable = false)
  private boolean includedInSettlement = false; // 정산 그룹에서 공유 여부

  @Column(name = "transacted_at", nullable = false)
  private LocalDateTime transactedAt; // 거래 일시

  @Builder
  public WalletTransaction(Wallet wallet, WalletTransactionType type, Long amount,
                           String description, boolean includedInSettlement, LocalDateTime transactedAt) {
    this.wallet = wallet;
    this.type = type;
    this.amount = amount;
    this.description = description;
    this.includedInSettlement = includedInSettlement;
    this.transactedAt = transactedAt;
  }

  public void includeInSettlement() {
    this.includedInSettlement = true;
  }
}

