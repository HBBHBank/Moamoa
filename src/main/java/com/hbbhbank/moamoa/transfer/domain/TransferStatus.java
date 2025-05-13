package com.hbbhbank.moamoa.transfer.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransferStatus {
  PENDING("PENDING", "송금 대기"),
  SUCCESS("SUCCESS", "송금 성공"),
  FAILED("FAILED", "송금 실패"),
  ;

  private final String code;
  private final String message;
}
