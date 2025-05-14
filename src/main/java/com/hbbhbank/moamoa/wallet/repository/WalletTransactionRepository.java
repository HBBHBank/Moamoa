package com.hbbhbank.moamoa.wallet.repository;

import com.hbbhbank.moamoa.wallet.domain.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTransactionRepository
  extends JpaRepository<WalletTransaction, Long>, WalletTransactionRepositoryCustom {
}
