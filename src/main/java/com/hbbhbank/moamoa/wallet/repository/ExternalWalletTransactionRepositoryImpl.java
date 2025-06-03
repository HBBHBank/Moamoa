package com.hbbhbank.moamoa.wallet.repository;

import com.hbbhbank.moamoa.settlement.domain.SettlementSharePeriod;
import com.hbbhbank.moamoa.wallet.domain.ExternalWalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.QExternalWalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class ExternalWalletTransactionRepositoryImpl implements ExternalWalletTransactionRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<ExternalWalletTransaction> findAllByPredicate(Predicate predicate) {
    return queryFactory
      .selectFrom(QExternalWalletTransaction.externalWalletTransaction)
      .where(predicate)
      .fetch();
  }

  @Override
  public List<ExternalWalletTransaction> findByWalletAndPeriods(Wallet wallet, List<SettlementSharePeriod> periods) {
    QExternalWalletTransaction tx = QExternalWalletTransaction.externalWalletTransaction;

    BooleanBuilder builder = new BooleanBuilder();
    for (SettlementSharePeriod period : periods) {
      builder.or(tx.transactedAt.between(
        period.getStartedAt(),
        period.getStoppedAt() != null ? period.getStoppedAt() : LocalDateTime.MAX
      ));
    }

    return queryFactory
      .selectFrom(tx)
      .where(
        tx.wallet.id.eq(wallet.getId()).and(builder)
      )
      .orderBy(tx.transactedAt.asc())
      .fetch();
  }
}
