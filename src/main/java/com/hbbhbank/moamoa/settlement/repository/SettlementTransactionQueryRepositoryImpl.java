package com.hbbhbank.moamoa.settlement.repository;

import com.hbbhbank.moamoa.settlement.domain.SettlementGroup;
import com.hbbhbank.moamoa.settlement.domain.SettlementSharePeriod;
import com.hbbhbank.moamoa.wallet.domain.QExternalWalletTransaction;
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
    QInternalWalletTransaction internalTx = QInternalWalletTransaction.internalWalletTransaction;
    QExternalWalletTransaction externalTx = QExternalWalletTransaction.externalWalletTransaction;
    QSettlementSharePeriod period = QSettlementSharePeriod.settlementSharePeriod;

    List<SettlementSharePeriod> periods = queryFactory
      .selectFrom(period)
      .where(period.group.eq(group))
      .fetch();

    if (periods.isEmpty()) {
      return BigDecimal.ZERO;
    }

    // 시간 조건 - 내부 거래
    BooleanBuilder timeCondition = new BooleanBuilder();
    for (SettlementSharePeriod p : periods) {
      LocalDateTime end = p.getStoppedAt() != null ? p.getStoppedAt() : LocalDateTime.MAX;
      timeCondition.or(internalTx.transactedAt.between(p.getStartedAt(), end));
    }

    // 내부 거래: wallet 또는 counterWallet이 공유 지갑
    BooleanBuilder internalWalletCondition = new BooleanBuilder()
      .or(internalTx.wallet.eq(group.getReferencedWallet()))
      .or(internalTx.counterWallet.eq(group.getReferencedWallet()));

    // 외부 거래 시간 조건
    BooleanBuilder externalTimeCondition = new BooleanBuilder();
    for (SettlementSharePeriod p : periods) {
      LocalDateTime end = p.getStoppedAt() != null ? p.getStoppedAt() : LocalDateTime.MAX;
      externalTimeCondition.or(externalTx.transactedAt.between(p.getStartedAt(), end));
    }

    // 외부 거래: 지갑이 공유 지갑
    BooleanBuilder externalWalletCondition = new BooleanBuilder()
      .and(externalTx.wallet.eq(group.getReferencedWallet()));

    // 내부 거래 총합
    BigDecimal internalSum = queryFactory
      .select(internalTx.amount.sum())
      .from(internalTx)
      .where(internalWalletCondition.and(timeCondition))
      .fetchOne();

    // 외부 거래 총합
    BigDecimal externalSum = queryFactory
      .select(externalTx.amount.sum())
      .from(externalTx)
      .where(externalWalletCondition.and(externalTimeCondition))
      .fetchOne();

    return safeSum(internalSum, externalSum);
  }

  private BigDecimal safeSum(BigDecimal a, BigDecimal b) {
    return (a != null ? a : BigDecimal.ZERO).add(b != null ? b : BigDecimal.ZERO);
  }
}
