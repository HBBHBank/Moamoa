package com.hbbhbank.moamoa.wallet.repository;

import com.hbbhbank.moamoa.wallet.domain.WalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.WalletTransactionType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.hbbhbank.moamoa.wallet.domain.QCurrency.currency;
import static com.hbbhbank.moamoa.wallet.domain.QWallet.wallet;
import static com.hbbhbank.moamoa.wallet.domain.QWalletTransaction.walletTransaction;

@Repository
@RequiredArgsConstructor
public class WalletTransactionRepositoryImpl implements WalletTransactionRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<WalletTransaction> findListByUserAndCurrency(Long userId, String currencyCode) {
    return List.of();
  }

  @Override
  public List<WalletTransaction> findListByUserAndCurrencyAndType(Long userId, String currencyCode, WalletTransactionType type) {
    return queryFactory
      .selectFrom(walletTransaction)
      .join(walletTransaction.wallet, wallet).fetchJoin()
      .join(wallet.currency, currency).fetchJoin()
      .where(
        wallet.user.id.eq(userId),
        wallet.currency.code.eq(currencyCode),
        walletTransaction.type.eq(type)
      )
      .orderBy(walletTransaction.transactedAt.desc()) // 최신 순
      .fetch();
  }

  @Override
  public Optional<WalletTransaction> findByCurrencyCode(String currencyCode) {
    return Optional.ofNullable(
      queryFactory
        .selectFrom(walletTransaction)
        .join(walletTransaction.wallet).fetchJoin()
        .where(walletTransaction.wallet.currency.code.eq(currencyCode))
        .orderBy(walletTransaction.transactedAt.desc())
        .fetchFirst()
    );
  }

}
