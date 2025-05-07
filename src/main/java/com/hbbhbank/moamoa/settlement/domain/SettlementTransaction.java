package com.hbbhbank.moamoa.settlement.domain;

import com.hbbhbank.moamoa.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "settlement_transactions")
public class SettlementTransaction { // 정산 결과 내역 (누가 누구에게 송금해야 하는지)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "settlement_transaction_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "settlement_group_id", nullable = false)
  private SettlementGroup group; // 여기서 toUser(=host)도 조회 가능.

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "from_user_id", nullable = false)
  private User fromUser;

  @Column(name = "settlement_amount", nullable = false)
  private Long amount; // 정산 해야 할 금액

  @Column(name = "is_transferred", nullable = false)
  private boolean isTransferred = false; // 송금 여부

  @CreationTimestamp
  @Column(name = "settlement_requested_at", nullable = false)
  private LocalDateTime requestedAt; // 정산 요청 시점

  @Column(name = "transferred_at")
  private LocalDateTime transferredAt; // 송금 완료 시점

  @Builder
  public SettlementTransaction(SettlementGroup group, User fromUser, Long amount) {
    this.group = group;
    this.fromUser = fromUser;
    this.amount = amount;
  }

  public void markTransferred(LocalDateTime transferredAt) {
    this.isTransferred = true;
    this.transferredAt = transferredAt;
  }
}

