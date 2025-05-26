package com.hbbhbank.moamoa.wallet.repository;

import com.hbbhbank.moamoa.wallet.domain.ExternalWalletTransaction;
import com.querydsl.core.types.Predicate;

import java.util.List;

public interface ExternalWalletTransactionRepositoryCustom {
  List<ExternalWalletTransaction> findAllByPredicate(Predicate predicate);
}
