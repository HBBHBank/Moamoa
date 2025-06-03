package com.hbbhbank.moamoa.settlement.repository;

import com.hbbhbank.moamoa.settlement.domain.QSettlementSharePeriod;
import com.hbbhbank.moamoa.settlement.domain.SettlementGroup;
import com.hbbhbank.moamoa.settlement.domain.SettlementSharePeriod;
import com.hbbhbank.moamoa.wallet.domain.QExternalWalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.QInternalWalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.domain.WalletTransactionType;
import com.hbbhbank.moamoa.wallet.repository.InternalWalletTransactionRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.hbbhbank.moamoa.wallet.domain.QInternalWalletTransaction.internalWalletTransaction;

@Repository
@RequiredArgsConstructor
public class SettlementTransactionQueryRepositoryImpl implements SettlementTransactionQueryRepository {

  private final InternalWalletTransactionRepository internalWalletTransactionRepository;
  private final SettlementSharePeriodRepository sharePeriodRepository;
  private final JPAQueryFactory queryFactory;

  @Override
  public BigDecimal sumByGroupSharePeriods(SettlementGroup group) {
    QInternalWalletTransaction internalTx = internalWalletTransaction;
    QExternalWalletTransaction externalTx = QExternalWalletTransaction.externalWalletTransaction;
    QSettlementSharePeriod period = QSettlementSharePeriod.settlementSharePeriod;

    List<SettlementSharePeriod> periods = queryFactory
      .selectFrom(period)
      .where(period.group.eq(group))
      .fetch();

    if (periods.isEmpty()) return BigDecimal.ZERO;

    BooleanBuilder timeCondition = new BooleanBuilder();
    for (SettlementSharePeriod p : periods) {
      LocalDateTime end = p.getStoppedAt() != null ? p.getStoppedAt() : LocalDateTime.MAX;
      timeCondition.or(internalTx.transactedAt.between(p.getStartedAt(), end));
    }

    BooleanBuilder internalWalletCondition = new BooleanBuilder()
      .or(internalTx.wallet.eq(group.getReferencedWallet()))
      .or(internalTx.counterWallet.eq(group.getReferencedWallet()));

    BooleanBuilder externalTimeCondition = new BooleanBuilder();
    for (SettlementSharePeriod p : periods) {
      LocalDateTime end = p.getStoppedAt() != null ? p.getStoppedAt() : LocalDateTime.MAX;
      externalTimeCondition.or(externalTx.transactedAt.between(p.getStartedAt(), end));
    }

    BooleanBuilder externalWalletCondition = new BooleanBuilder()
      .and(externalTx.wallet.eq(group.getReferencedWallet()));

    BigDecimal internalSum = queryFactory
      .select(internalTx.amount.sum())
      .from(internalTx)
      .where(internalWalletCondition.and(timeCondition))
      .fetchOne();

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

  // 공유 주기 내 출금 합계
  public BigDecimal sumOnlyExpensesByPeriods(Wallet wallet, List<SettlementSharePeriod> periods) {
    if (periods.isEmpty()) return BigDecimal.ZERO;

    BooleanBuilder builder = new BooleanBuilder();
    for (SettlementSharePeriod period : periods) {
      LocalDateTime stoppedAt = period.getStoppedAt() != null ? period.getStoppedAt() : LocalDateTime.now();
      builder.or(internalWalletTransaction.transactedAt.between(period.getStartedAt(), stoppedAt));
    }

    List<WalletTransactionType> expenseTypes = Arrays.stream(WalletTransactionType.values())
      .filter(WalletTransactionType::isExpenseType)
      .toList();

    return internalWalletTransactionRepository
      .sumAmountByWalletAndTypesAndPeriods(wallet, expenseTypes, builder)
      .orElse(BigDecimal.ZERO);
  }

  // 공유 주기 내 입금 합계
  public BigDecimal sumOnlyIncomeByPeriods(Wallet wallet, List<SettlementSharePeriod> periods) {
    if (periods.isEmpty()) return BigDecimal.ZERO;

    BooleanBuilder builder = new BooleanBuilder();
    for (SettlementSharePeriod period : periods) {
      LocalDateTime stoppedAt = period.getStoppedAt() != null ? period.getStoppedAt() : LocalDateTime.now();
      builder.or(internalWalletTransaction.transactedAt.between(period.getStartedAt(), stoppedAt));
    }

    List<WalletTransactionType> incomeTypes = Arrays.stream(WalletTransactionType.values())
      .filter(WalletTransactionType::isIncomeType)
      .toList();

    return internalWalletTransactionRepository
      .sumAmountByWalletAndTypesAndPeriods(wallet, incomeTypes, builder)
      .orElse(BigDecimal.ZERO);
  }

  // 공유 주기 전체를 기준으로 출금+입금 합산
  @Override
  public BigDecimal sumNetSettlementAmount(SettlementGroup group) {
    List<SettlementSharePeriod> periods = sharePeriodRepository.findAllByGroup(group);
    Wallet wallet = group.getReferencedWallet();

    BigDecimal totalExpense = sumOnlyExpensesByPeriods(wallet, periods); // 출금
    BigDecimal totalIncome = sumOnlyIncomeByPeriods(wallet, periods);   // 입금

    return safeSum(totalExpense, totalIncome);
  }
}
