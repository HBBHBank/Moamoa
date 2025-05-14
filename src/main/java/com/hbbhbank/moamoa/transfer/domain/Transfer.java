package com.hbbhbank.moamoa.transfer.domain;

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
@Table(name = "transfers")
public class Transfer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "from_wallet_id", nullable = false)
  private Wallet fromWallet; // 보내는 지갑

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "to_wallet_id")
  private Wallet toWallet; // 받는 지갑

  @Column(name = "transfer_amount", nullable = false)
  private BigDecimal amount; // 송금 금액

  @Enumerated(EnumType.STRING)
  @Column(name = "transfer_type", nullable = false)
  private TransferStatus status; // PENDING, SUCCESS, FAILED

  @Column(name = "transfer_at", nullable = false)
  private LocalDateTime transferAt; // 송금 완료 시각

  @Builder
  public Transfer(Wallet fromWallet, Wallet toWallet, BigDecimal amount, TransferStatus status, LocalDateTime transferAt) {
    this.fromWallet = fromWallet;
    this.toWallet = toWallet;
    this.amount = amount;
    this.status = status;
    this.transferAt = transferAt;
  }

  public static Transfer create(Wallet fromWallet, Wallet toWallet, BigDecimal amount, TransferStatus status) {
    return Transfer.builder()
      .fromWallet(fromWallet)
      .toWallet(toWallet)
      .amount(amount)
      .status(status)
      .transferAt(LocalDateTime.now())
      .build();
  }
}
