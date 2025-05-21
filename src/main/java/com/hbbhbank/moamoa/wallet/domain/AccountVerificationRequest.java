package com.hbbhbank.moamoa.wallet.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "account_verification_requests")
public class AccountVerificationRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "transaction_id", nullable = false, unique = true)
  private String transactionId;

  private Long userId;

  @Builder
  public AccountVerificationRequest(String transactionId) {
    this.transactionId = transactionId;
  }

  public static AccountVerificationRequest from(String transactionId) {
    return new AccountVerificationRequest(transactionId);
  }
}