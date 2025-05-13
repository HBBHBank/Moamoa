package com.hbbhbank.moamoa.wallet.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WalletTransactionType {
  QR_PAYMENT("QR_PAYMENT", "QR 결제"),
  WITHDRAWAL("WITHDRAWAL","출금"),
  TRANSFER("TRANSFER", "송금"),
  AUTO_CHARGE("AUTO_CHARGE", "자동 충전"),
  MANUAL_CHARGE("MANUAL_CHARGE", "수동 충전"),
  SETTLEMENT_SEND("SETTLEMENT_SEND", "정산 송금"),
  SETTLEMENT_RECEIVE("SETTLEMENT_RECEIVE", "정산 수금"),
  EXCHANGE_OUT("EXCHANGE_OUT", "환전 출금"),
  EXCHANGE_IN("EXCHANGE_IN", "환전 입금");

  private final String code;
  private final String message;
}