package com.hbbhbank.moamoa.wallet.repository;

import com.hbbhbank.moamoa.wallet.domain.WalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.WalletTransactionType;

import java.util.List;
import java.util.Optional;

public interface WalletTransactionRepositoryCustom {

  List<WalletTransaction> findListByUserAndCurrency(Long userId, String currencyCode);

  List<WalletTransaction> findListByUserAndCurrencyAndType(Long userId, String currencyCode, WalletTransactionType type);

  Optional<WalletTransaction> findByCurrencyCode(String currencyCode);
}
