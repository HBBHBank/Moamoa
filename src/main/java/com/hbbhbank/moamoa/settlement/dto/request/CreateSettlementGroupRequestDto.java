package com.hbbhbank.moamoa.settlement.dto.request;

public record CreateSettlementGroupRequestDto(
  String currencyCode, // 공유 지갑 선택 (통화 선택)
  String groupName // 그룹 이름
) {
}
