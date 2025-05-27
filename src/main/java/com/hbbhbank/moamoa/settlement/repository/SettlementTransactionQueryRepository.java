package com.hbbhbank.moamoa.settlement.repository;

import com.hbbhbank.moamoa.settlement.domain.SettlementGroup;

import java.math.BigDecimal;

public interface SettlementTransactionQueryRepository {
  BigDecimal sumByGroupSharePeriods(SettlementGroup group);
}
