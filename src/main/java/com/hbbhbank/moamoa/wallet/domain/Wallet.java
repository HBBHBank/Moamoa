package com.hbbhbank.moamoa.wallet.domain;

import com.hbbhbank.moamoa.external.domain.UserAccountLink;
import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.user.domain.User;
import com.hbbhbank.moamoa.wallet.exception.WalletErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "wallets", uniqueConstraints = {
  @UniqueConstraint(columnNames = {"user_id", "currency_code"})
}) // 하나의 사용자는 같은 통화의 지갑을 2개 이상 가질 수 없도록 설정.
public class Wallet {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "wallet_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "account_number", nullable = false, length = 30)
  private String accountNumber; // 지갑 계좌번호. 환비 API와 별도의 계좌 번호임. (확장성 고려)

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "currency_code")
  private Currency currency;

  @Column(name = "balance", nullable = false)
  private BigDecimal balance; // 단위: 포인트

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_account_link_id", unique = true)
  private UserAccountLink accountLink;

  @Builder
  public Wallet(User user, String accountNumber, Currency currency, BigDecimal balance, UserAccountLink accountLink) {
    this.user = user;
    this.accountNumber = accountNumber;
    this.currency = currency;
    this.balance = balance;
    this.accountLink = accountLink;
  }

  public void updateBalance(BigDecimal amount) {
    this.balance = this.balance.add(amount);
  }

  public void subtractBalance(BigDecimal amount) {
    if (this.balance.compareTo(amount) < 0) {
      throw BaseException.type(WalletErrorCode.INSUFFICIENT_BALANCE);
    }
    this.balance = this.balance.subtract(amount);
  }

}

