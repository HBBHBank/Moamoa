package com.hbbhbank.moamoa.transfer.service;

import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.transfer.domain.Transfer;
import com.hbbhbank.moamoa.transfer.domain.TransferStatus;
import com.hbbhbank.moamoa.transfer.dto.request.PointTransferRequestDto;
import com.hbbhbank.moamoa.transfer.exception.TransferErrorCode;
import com.hbbhbank.moamoa.transfer.repository.PointTransferRepository;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PointTransferService {

  private final WalletRepository walletRepository;
  private final PointTransferRepository pointTransferRepository;

  @Transactional
  public void transferPoints(PointTransferRequestDto req) {
    Wallet fromWallet = walletRepository.findById(req.fromWalletId())
      .orElseThrow(() -> BaseException.type(TransferErrorCode.WALLET_NOT_FOUND));

    Wallet toWallet = walletRepository.findById(req.toWalletId())
      .orElseThrow(() -> BaseException.type(TransferErrorCode.WALLET_NOT_FOUND));

    if (!fromWallet.getCurrency().getCode().equals(toWallet.getCurrency().getCode())) {
      throw BaseException.type(TransferErrorCode.CURRENCY_MISMATCH);
    }

    if (fromWallet.getBalance().compareTo(req.amount()) < 0) {
      throw BaseException.type(TransferErrorCode.INSUFFICIENT_BALANCE);
    }

    fromWallet.subtractBalance(req.amount());
    toWallet.updateBalance(req.amount());

    pointTransferRepository.save(
      Transfer.builder()
        .fromWallet(fromWallet)
        .toWallet(toWallet)
        .amount(req.amount())
        .status(TransferStatus.SUCCESS)
        .transferAt(LocalDateTime.now())
        .build()
    );
  }
}
