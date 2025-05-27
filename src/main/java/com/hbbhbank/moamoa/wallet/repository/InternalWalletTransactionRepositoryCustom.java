package com.hbbhbank.moamoa.wallet.repository;

import com.hbbhbank.moamoa.settlement.domain.SettlementSharePeriod;
import com.hbbhbank.moamoa.wallet.domain.InternalWalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.querydsl.core.types.Predicate;

import java.util.List;

public interface InternalWalletTransactionRepositoryCustom {
  List<InternalWalletTransaction> findAllByPredicate(Predicate predicate);
  List<InternalWalletTransaction> findByWalletAndPeriods(Wallet wallet, List<SettlementSharePeriod> periods);
}
