package com.hbbhbank.moamoa.wallet.repository;

import com.hbbhbank.moamoa.wallet.domain.Wallet;

import java.util.List;
import java.util.Optional;

public interface WalletRepositoryCustom {
  Wallet findByIdOrThrow(Long walletId);
  Optional<Wallet> findByUserIdAndCurrencyCode(Long userId, String currencyCode);
  boolean existsByUserIdAndCurrencyCode(Long userId, String currencyCode);
  List<Wallet> findAllByUserWithCurrency(Long userId);
}
