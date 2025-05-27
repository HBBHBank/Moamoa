package com.hbbhbank.moamoa.settlement.repository;

import com.hbbhbank.moamoa.settlement.domain.SettlementGroup;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettlementGroupRepository extends JpaRepository<SettlementGroup, Long> {
  boolean existsByReferencedWallet(Wallet wallet);
  Optional<SettlementGroup> findByJoinCode(String joinCode);
}
