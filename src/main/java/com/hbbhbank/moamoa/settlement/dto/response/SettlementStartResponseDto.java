package com.hbbhbank.moamoa.settlement.dto.response;

import java.util.List;

public record SettlementStartResponseDto(
  List<Long> selectedMembers,
  Long settlementAmount
) {
}
