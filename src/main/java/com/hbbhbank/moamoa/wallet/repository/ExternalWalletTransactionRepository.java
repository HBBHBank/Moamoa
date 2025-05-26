package com.hbbhbank.moamoa.wallet.repository;

import com.hbbhbank.moamoa.wallet.domain.ExternalWalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalWalletTransactionRepository extends JpaRepository<ExternalWalletTransaction, Long>, ExternalWalletTransactionRepositoryCustom {
}
