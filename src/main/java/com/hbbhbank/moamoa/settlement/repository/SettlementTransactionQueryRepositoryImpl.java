package com.hbbhbank.moamoa.settlement.repository;

import com.hbbhbank.moamoa.settlement.domain.SettlementGroup;
import com.hbbhbank.moamoa.settlement.domain.SettlementSharePeriod;
import com.hbbhbank.moamoa.wallet.domain.InternalWalletTransaction;
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

@Repository
@RequiredArgsConstructor
public class SettlementTransactionQueryRepositoryImpl implements SettlementTransactionQueryRepository {

  private final InternalWalletTransactionRepository internalWalletTransactionRepository;
  private final SettlementSharePeriodRepository sharePeriodRepository;
  private final JPAQueryFactory queryFactory;

  @Override
  public BigDecimal sumByGroupSharePeriods(SettlementGroup group) {
    List<SettlementSharePeriod> periods = sharePeriodRepository.findAllByGroup(group);
    Wallet wallet = group.getReferencedWallet();

    // 공유 지갑에서 발생한 출금 거래만 필터링
    List<InternalWalletTransaction> outgoingTx = findSharedOutgoingTransactions(wallet, periods);

    return outgoingTx.stream()
      .map(InternalWalletTransaction::getAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private List<InternalWalletTransaction> findSharedOutgoingTransactions(Wallet sharedWallet, List<SettlementSharePeriod> periods) {
    QInternalWalletTransaction tx = QInternalWalletTransaction.internalWalletTransaction;

    BooleanBuilder periodBuilder = new BooleanBuilder();
    for (SettlementSharePeriod period : periods) {
      LocalDateTime end = period.getStoppedAt() != null ? period.getStoppedAt() : LocalDateTime.now();
      periodBuilder.or(tx.transactedAt.between(period.getStartedAt(), end));
    }

    BooleanBuilder typeCondition = new BooleanBuilder()
      .and(tx.type.in(
        List.of(
          WalletTransactionType.QR_PAYMENT,
          WalletTransactionType.TRANSFER_OUT,
          WalletTransactionType.SETTLEMENT_SEND
        )
      ));

    BooleanBuilder walletCondition = new BooleanBuilder()
      .and(tx.wallet.eq(sharedWallet));

    return queryFactory
      .selectFrom(tx)
      .where(walletCondition.and(typeCondition).and(periodBuilder))
      .fetch();
  }

  public BigDecimal sumOnlyExpensesByPeriods(Wallet wallet, List<SettlementSharePeriod> periods) {
    if (periods.isEmpty()) return BigDecimal.ZERO;

    BooleanBuilder builder = new BooleanBuilder();
    for (SettlementSharePeriod period : periods) {
      LocalDateTime stoppedAt = period.getStoppedAt() != null ? period.getStoppedAt() : LocalDateTime.now();
      builder.or(QInternalWalletTransaction.internalWalletTransaction.transactedAt.between(period.getStartedAt(), stoppedAt));
    }

    List<WalletTransactionType> expenseTypes = Arrays.stream(WalletTransactionType.values())
      .filter(WalletTransactionType::isExpenseType)
      .toList();

    return internalWalletTransactionRepository
      .sumAmountByWalletAndTypesAndPeriods(wallet, expenseTypes, builder)
      .orElse(BigDecimal.ZERO);
  }

  public BigDecimal sumOnlyIncomeByPeriods(Wallet wallet, List<SettlementSharePeriod> periods) {
    if (periods.isEmpty()) return BigDecimal.ZERO;

    BooleanBuilder builder = new BooleanBuilder();
    for (SettlementSharePeriod period : periods) {
      LocalDateTime stoppedAt = period.getStoppedAt() != null ? period.getStoppedAt() : LocalDateTime.now();
      builder.or(QInternalWalletTransaction.internalWalletTransaction.transactedAt.between(period.getStartedAt(), stoppedAt));
    }

    List<WalletTransactionType> incomeTypes = Arrays.stream(WalletTransactionType.values())
      .filter(WalletTransactionType::isIncomeType)
      .toList();

    return internalWalletTransactionRepository
      .sumAmountByWalletAndTypesAndPeriods(wallet, incomeTypes, builder)
      .orElse(BigDecimal.ZERO);
  }

  @Override
  public BigDecimal sumNetSettlementAmount(SettlementGroup group) {
    List<SettlementSharePeriod> periods = sharePeriodRepository.findAllByGroup(group);
    Wallet wallet = group.getReferencedWallet();

    BigDecimal totalExpense = sumOnlyExpensesByPeriods(wallet, periods);
    BigDecimal totalIncome = sumOnlyIncomeByPeriods(wallet, periods);

    return safeSum(totalExpense, totalIncome);
  }

  private BigDecimal safeSum(BigDecimal a, BigDecimal b) {
    return (a != null ? a : BigDecimal.ZERO).add(b != null ? b : BigDecimal.ZERO);
  }
}
