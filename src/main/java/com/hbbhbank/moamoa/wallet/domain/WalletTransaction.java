package com.hbbhbank.moamoa.wallet.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
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
  private Wallet wallet; // 주체 지갑

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "counter_wallet_id")
  private Wallet counterWallet; // 상대 지갑 (송금/결제 시)

  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_type", nullable = false)
  private WalletTransactionType type; // 거래 유형

  @Column(name = "transaction_amount", nullable = false)
  private BigDecimal amount; // 금액 (양수/음수로 입출금 표현)

  @Column(name = "included_in_settlement", nullable = false)
  private boolean includedInSettlement = false; // 정산 포함 여부

  @Column(name = "transacted_at", nullable = false, updatable = false)
  private LocalDateTime transactedAt; // 거래 일시

  @PrePersist
  public void prePersist() {
    this.transactedAt = LocalDateTime.now();
  }

  @Builder
  public WalletTransaction(Wallet wallet, Wallet counterWallet, WalletTransactionType type, BigDecimal amount, boolean includedInSettlement) {
    this.wallet = wallet;
    this.counterWallet = counterWallet;
    this.type = type;
    this.amount = amount;
    this.includedInSettlement = includedInSettlement;
  }

  public static WalletTransaction create(
    Wallet wallet,
    Wallet counterWallet,
    WalletTransactionType type,
    BigDecimal amount,
    boolean includedInSettlement
  ) {
    return WalletTransaction.builder()
      .wallet(wallet)
      .counterWallet(counterWallet)
      .type(type)
      .amount(amount)
      .includedInSettlement(includedInSettlement)
      .build();
  }


  // 정산 포함 처리
  public void includeInSettlement() {
    this.includedInSettlement = true;
  }
}