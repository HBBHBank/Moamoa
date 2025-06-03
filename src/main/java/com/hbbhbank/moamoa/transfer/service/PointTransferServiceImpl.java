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

    // 동일 사용자 지갑 간 송금 차단
    if (fromWallet.getUser().getId().equals(toWallet.getUser().getId())) {
      throw new BaseException(TransferErrorCode.CANNOT_TRANSFER_TO_SELF);
    }

    // 통화 코드가 다른 경우 차단
    if (!fromWallet.getCurrency().equals(toWallet.getCurrency())) {
      throw new BaseException(TransferErrorCode.CURRENCY_MISMATCH);
    }

    // 잔액 부족 시 차단
    if (fromWallet.getBalance().compareTo(dto.amount()) < 0) {
      throw new BaseException(TransferErrorCode.INSUFFICIENT_BALANCE);
    }

    // 금액 이동
    fromWallet.decreaseBalance(dto.amount());
    toWallet.increaseBalance(dto.amount());

    // 거래 기록 저장
    InternalWalletTransaction transaction = InternalWalletTransaction.create(
      fromWallet,
      toWallet,
      WalletTransactionType.TRANSFER_OUT,
      WalletTransactionStatus.SUCCESS,
      dto.amount()
    );
    walletTransactionRepository.save(transaction);

    InternalWalletTransaction counterTransaction = InternalWalletTransaction.create(
      toWallet,
      fromWallet,
      WalletTransactionType.TRANSFER_IN,
      WalletTransactionStatus.SUCCESS,
      dto.amount()
    );
    walletTransactionRepository.save(counterTransaction);

    return new PointTransferResponseDto(
      toWallet.getUser().getName(),
      toWallet.getWalletNumber(),
      dto.amount(),
      toWallet.getCurrency().getName()
    );
  }
}
