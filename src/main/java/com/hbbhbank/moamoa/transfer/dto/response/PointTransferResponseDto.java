package com.hbbhbank.moamoa.transfer.dto.response;

import com.hbbhbank.moamoa.transfer.domain.Transfer;
import com.hbbhbank.moamoa.transfer.domain.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PointTransferResponseDto(
  Long fromWalletId,
  Long toWalletId,
  BigDecimal amount,
  TransferStatus status,
  LocalDateTime transferAt
) {
  public static PointTransferResponseDto from(Transfer transfer) {
    return new PointTransferResponseDto(
      transfer.getFromWallet().getId(),
      transfer.getToWallet().getId(),
      transfer.getAmount(),
      transfer.getStatus(),
      transfer.getTransferAt()
    );
  }
}
