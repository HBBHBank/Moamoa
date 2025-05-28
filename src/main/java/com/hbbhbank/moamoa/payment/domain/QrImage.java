package com.hbbhbank.moamoa.payment.domain;

import com.hbbhbank.moamoa.wallet.domain.Wallet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class QrImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "qr_image_id")
  private Long id;

  @Column(name = "uuid", nullable = false, unique = true)
  private String uuid;

  @Lob
  @Column(name = "qr_image", columnDefinition = "MEDIUMBLOB")
  private byte[] qrImage;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "wallet_id")
  private Wallet wallet;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
    this.expiresAt = this.createdAt.plusMinutes(10); // 10분 유효
  }
}
