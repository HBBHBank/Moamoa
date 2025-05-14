package com.hbbhbank.moamoa.wallet.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WalletTransactionType {
  QR_PAYMENT("QR_PAYMENT", "QR 결제"),
  WITHDRAWAL("WITHDRAWAL","출금"),
  TRANSFER_OUT("TRANSFER_OUT", "송금 출금"),
  TRANSFER_IN("TRANSFER_IN", "송금 입금"),
  AUTO_CHARGE("AUTO_CHARGE", "자동 충전"),
  MANUAL_CHARGE("MANUAL_CHARGE", "수동 충전"),
  SETTLEMENT_SEND("SETTLEMENT_SEND", "정산 송금"),
  SETTLEMENT_RECEIVE("SETTLEMENT_RECEIVE", "정산 수금"),
  EXCHANGE_OUT("EXCHANGE_OUT", "환전 출금"),
  EXCHANGE_IN("EXCHANGE_IN", "환전 입금");

  private final String code;
  private final String message;

  public boolean isIncomeType() {
    return switch (this) {
      case AUTO_CHARGE, MANUAL_CHARGE, TRANSFER_IN, SETTLEMENT_RECEIVE, EXCHANGE_IN -> true;
      default -> false;
    };
  }

  public boolean isExpenseType() {
    return switch (this) {
      case QR_PAYMENT, WITHDRAWAL, TRANSFER_OUT, SETTLEMENT_SEND, EXCHANGE_OUT -> true;
      default -> false;
    };
  }
}