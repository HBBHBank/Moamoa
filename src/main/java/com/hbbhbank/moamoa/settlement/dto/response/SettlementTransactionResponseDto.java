package com.hbbhbank.moamoa.settlement.dto.response;

public record SettlementTransactionResponseDto(
  Long fromUserId,
  Long toUserId,
  Long amount,
  boolean isTransferred
  // 총 정산 금액
  // 정산 인원
  // 1인당 정산 금액
) {}
