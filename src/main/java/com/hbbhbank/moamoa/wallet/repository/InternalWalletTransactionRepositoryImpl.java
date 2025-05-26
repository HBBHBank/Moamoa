package com.hbbhbank.moamoa.wallet.repository;

import com.hbbhbank.moamoa.wallet.domain.InternalWalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.QInternalWalletTransaction;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class InternalWalletTransactionRepositoryImpl implements InternalWalletTransactionRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<InternalWalletTransaction> findAllByPredicate(Predicate predicate) {
    return queryFactory
      .selectFrom(QInternalWalletTransaction.internalWalletTransaction)
      .where(predicate)
      .fetch();
  }
}
