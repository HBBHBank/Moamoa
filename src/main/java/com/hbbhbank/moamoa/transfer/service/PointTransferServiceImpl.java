package com.hbbhbank.moamoa.transfer.service;

import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.transfer.dto.request.PointTransferRequestDto;
import com.hbbhbank.moamoa.transfer.dto.response.PointTransferResponseDto;
import com.hbbhbank.moamoa.transfer.exception.TransferErrorCode;
import com.hbbhbank.moamoa.wallet.domain.InternalWalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.domain.WalletTransactionStatus;
import com.hbbhbank.moamoa.wallet.domain.WalletTransactionType;
import com.hbbhbank.moamoa.wallet.repository.InternalWalletTransactionRepository;
import com.hbbhbank.moamoa.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointTransferServiceImpl implements PointTransferService {

  private final WalletRepository walletRepository;
  private final InternalWalletTransactionRepository walletTransactionRepository;

  @Override
  @Transactional
  public PointTransferResponseDto transferByUser(PointTransferRequestDto dto) {
    Wallet fromWallet = walletRepository.findByWalletNumber(dto.fromWalletNumber())
      .orElseThrow(() -> new BaseException(TransferErrorCode.WALLET_NOT_FOUND));

    Wallet toWallet = walletRepository.findByWalletNumber(dto.toWalletNumber())
      .orElseThrow(() -> new BaseException(TransferErrorCode.WALLET_NOT_FOUND));

    if (!fromWallet.getCurrency().equals(toWallet.getCurrency())) {
      throw new BaseException(TransferErrorCode.CURRENCY_MISMATCH);
    }

    if (fromWallet.getBalance().compareTo(dto.amount()) < 0) {
      throw new BaseException(TransferErrorCode.INSUFFICIENT_BALANCE);
    }

    fromWallet.decreaseBalance(dto.amount());
    toWallet.increaseBalance(dto.amount());

    InternalWalletTransaction transaction = InternalWalletTransaction.create(
      fromWallet,
      toWallet,
      WalletTransactionType.TRANSFER_OUT,
      WalletTransactionStatus.SUCCESS,
      dto.amount()
    );

    walletTransactionRepository.save(transaction);

    return new PointTransferResponseDto(
      toWallet.getUser().getName(),        // 받는 사람 이름
      toWallet.getWalletNumber(),          // 받는 지갑 번호
      dto.amount(),                        // 보낸 금액
      toWallet.getCurrency().getName()     // 통화 코드
    );
  }
}