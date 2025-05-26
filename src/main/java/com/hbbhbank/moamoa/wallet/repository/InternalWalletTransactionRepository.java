package com.hbbhbank.moamoa.wallet.repository;

import com.hbbhbank.moamoa.wallet.domain.InternalWalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternalWalletTransactionRepository extends JpaRepository<InternalWalletTransaction, Long>, InternalWalletTransactionRepositoryCustom {
}