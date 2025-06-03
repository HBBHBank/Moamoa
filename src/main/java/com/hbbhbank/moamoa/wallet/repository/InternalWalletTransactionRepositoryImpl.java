package com.hbbhbank.moamoa.wallet.repository;

import com.hbbhbank.moamoa.settlement.domain.SettlementSharePeriod;
import com.hbbhbank.moamoa.wallet.domain.InternalWalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.QInternalWalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class InternalWalletTransactionRepositoryImpl implements InternalWalletTransactionRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  // 공유 지갑 거래 내역을 Predicate를 통해 조회하는 메서드
  @Override
  public List<InternalWalletTransaction> findAllByPredicate(Predicate predicate) {
    return queryFactory
      .selectFrom(QInternalWalletTransaction.internalWalletTransaction)
      .where(predicate)
      .fetch();
  }

  @Override
  public List<InternalWalletTransaction> findByWalletAndPeriods(Wallet wallet, List<SettlementSharePeriod> periods) {
    QInternalWalletTransaction tx = QInternalWalletTransaction.internalWalletTransaction;

    BooleanBuilder builder = new BooleanBuilder();
    for (SettlementSharePeriod period : periods) {
      builder.or(
        tx.transactedAt.between(
          period.getStartedAt(),
          period.getStoppedAt() != null ? period.getStoppedAt() : LocalDateTime.MAX
        )
      );
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
