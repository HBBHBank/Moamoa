package com.hbbhbank.moamoa.transfer.dto.response;

import com.hbbhbank.moamoa.transfer.domain.Transfer;
import com.hbbhbank.moamoa.transfer.domain.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PointTransferResponseDto(
  String fromWalletNumber,
  String fromUserName,
  String toWalletNumber,
  String toUserName,
  BigDecimal amount,
  TransferStatus status,
  LocalDateTime transferAt
) {
  public static PointTransferResponseDto from(Transfer transfer) {
    return new PointTransferResponseDto(
      transfer.getFromWallet().getWalletNumber(),
      transfer.getFromWallet().getUser().getName(),
      transfer.getToWallet().getWalletNumber(),
      transfer.getToWallet().getUser().getName(),
      transfer.getAmount(),
      transfer.getStatus(),
      transfer.getTransferAt()
    );
  }
}
