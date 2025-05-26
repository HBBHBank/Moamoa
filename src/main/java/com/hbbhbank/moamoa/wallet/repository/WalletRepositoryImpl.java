package com.hbbhbank.moamoa.wallet.repository;

import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.hbbhbank.moamoa.wallet.domain.QCurrency.currency;
import static com.hbbhbank.moamoa.wallet.domain.QWallet.wallet;

@Repository
@RequiredArgsConstructor
public class WalletRepositoryImpl implements WalletRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Optional<Wallet> findByUserIdAndCurrencyCode(Long userId, String currencyCode) {
    return Optional.ofNullable(
      queryFactory
        .selectFrom(wallet)
        .join(wallet.currency, currency).fetchJoin()
        .where(
          wallet.user.id.eq(userId),
          wallet.currency.code.eq(currencyCode)
        )
        .fetchOne()
    );
  }

  @Override
  public boolean existsByUserIdAndCurrencyCode(Long userId, String currencyCode) {
    Integer result = queryFactory
      .selectOne()
      .from(wallet)
      .where(
        wallet.user.id.eq(userId),
        wallet.currency.code.eq(currencyCode)
      )
      .fetchFirst(); // 성능상 fetchFirst가 더 가볍다

    return result != null;
  }

  @Override
  public List<Wallet> findAllByUser(Long userId) {
    return queryFactory
      .selectFrom(wallet)
      .join(wallet.currency, currency).fetchJoin()
      .where(wallet.user.id.eq(userId))
      .fetch();
  }

  @Override
  public Optional<Wallet> findByWalletNumber(String walletNumber) {
    return Optional.ofNullable(
      queryFactory.selectFrom(wallet)
        .join(wallet.currency, currency).fetchJoin()
        .where(wallet.walletNumber.eq(walletNumber))
        .fetchOne()
    );
  }
}
