package com.hbbhbank.moamoa.wallet.repository;

import com.hbbhbank.moamoa.wallet.domain.ExternalWalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.QExternalWalletTransaction;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

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
}
