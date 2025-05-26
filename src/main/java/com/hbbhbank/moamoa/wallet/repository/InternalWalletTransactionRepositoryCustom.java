package com.hbbhbank.moamoa.wallet.repository;

import com.hbbhbank.moamoa.wallet.domain.InternalWalletTransaction;
import com.querydsl.core.types.Predicate;

import java.util.List;

public interface InternalWalletTransactionRepositoryCustom {
  List<InternalWalletTransaction> findAllByPredicate(Predicate predicate);
}
