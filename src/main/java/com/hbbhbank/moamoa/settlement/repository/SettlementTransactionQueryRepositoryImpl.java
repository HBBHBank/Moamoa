package com.hbbhbank.moamoa.settlement.repository;

import com.hbbhbank.moamoa.settlement.domain.SettlementGroup;
import com.hbbhbank.moamoa.settlement.domain.SettlementSharePeriod;
import com.hbbhbank.moamoa.wallet.domain.QInternalWalletTransaction;
import com.hbbhbank.moamoa.settlement.domain.QSettlementSharePeriod;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SettlementTransactionQueryRepositoryImpl implements SettlementTransactionQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public BigDecimal sumByGroupSharePeriods(SettlementGroup group) {
    QInternalWalletTransaction tx = QInternalWalletTransaction.internalWalletTransaction;
    QSettlementSharePeriod period = QSettlementSharePeriod.settlementSharePeriod;

    List<SettlementSharePeriod> periods = queryFactory
      .selectFrom(period)
      .where(period.group.eq(group))
      .fetch();

    BooleanBuilder timeCondition = new BooleanBuilder();
    for (SettlementSharePeriod p : periods) {
      LocalDateTime stop = p.getStoppedAt() != null ? p.getStoppedAt() : LocalDateTime.now();
      timeCondition.or(tx.transactedAt.between(p.getStartedAt(), stop));
    }

    return queryFactory
      .select(tx.amount.sum())
      .from(tx)
      .where(tx.wallet.eq(group.getReferencedWallet()).and(timeCondition))
      .fetchOne();
  }
}
