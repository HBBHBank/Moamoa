package com.hbbhbank.moamoa.settlement.dto.response;

import com.hbbhbank.moamoa.settlement.domain.GroupStatus;
import com.hbbhbank.moamoa.settlement.domain.SettlementGroup;
import com.hbbhbank.moamoa.settlement.domain.SettlementStatus;

import java.time.LocalDateTime;
import java.util.List;

public record SettlementGroupResponseDto(
  Long id,
  String name,
  boolean isActive,
  boolean isOwner,
  int memberCount,
  int maxMembers,
  String currencyCode,
  String currencyName,
  SettlementStatus settlementStatus,
  LocalDateTime createdAt,
  List<SettlementMemberDto> members,
  HostDto host
) {
  public static SettlementGroupResponseDto from(SettlementGroup group, Long currentUserId) {
    return new SettlementGroupResponseDto(
      group.getId(),
      group.getGroupName(),
      group.getGroupStatus() == GroupStatus.ACTIVE,
      group.getHost().getId().equals(currentUserId),
      group.getMembers().size(),
      group.getMaxMembers(),
      group.getReferencedWallet().getCurrency().getCode(),
      group.getReferencedWallet().getCurrency().getName(),
      group.getSettlementStatus(),
      group.getCreatedAt(),
      group.getMembers().stream().map(SettlementMemberDto::from).toList(),
      HostDto.from(group.getHost())
    );
  }
}

