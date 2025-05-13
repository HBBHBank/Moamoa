package com.hbbhbank.moamoa.wallet.service;

import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.domain.WalletTransaction;
import com.hbbhbank.moamoa.wallet.dto.request.CreateWalletTransactionRequestDto;
import com.hbbhbank.moamoa.wallet.dto.request.WalletInquiryRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.WalletTransactionResponseDto;
import com.hbbhbank.moamoa.wallet.exception.WalletErrorCode;
import com.hbbhbank.moamoa.wallet.repository.WalletRepository;
import com.hbbhbank.moamoa.wallet.repository.WalletTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletTransactionService {

  private final WalletTransactionRepository walletTransactionRepository;
  private final WalletRepository walletRepository;

  public WalletTransaction showWalletTransaction(WalletInquiryRequestDto req) {
    return walletTransactionRepository.findByWallet_Currency_Code(req.currencyCode())
      .orElseThrow(() -> BaseException.type(WalletErrorCode.NOT_FOUND_WALLET));
  }

  @Transactional
  public WalletTransactionResponseDto recordTransaction(CreateWalletTransactionRequestDto req) {
    // 1. 지갑 조회
    Wallet wallet = walletRepository.findByIdOrThrow(req.walletId());

    // 2. 상대 지갑
    Wallet counter = req.counterWalletId() != null
      ? walletRepository.findByIdOrThrow(req.counterWalletId())
      : null;

    // 3. 거래 유형에 따라 입금/출금 처리
    switch (req.type()) {
      case AUTO_CHARGE, MANUAL_CHARGE, SETTLEMENT_RECEIVE, EXCHANGE_IN -> wallet.increaseBalance(req.amount());
      case QR_PAYMENT, WITHDRAWAL, TRANSFER, SETTLEMENT_SEND, EXCHANGE_OUT -> wallet.decreaseBalance(req.amount());
      default -> throw BaseException.type(WalletErrorCode.INVALID_TRANSACTION_TYPE); // 안전망
    }

    // 4. 트랜잭션 정적 팩토리 메서드로 생성
    WalletTransaction tx = WalletTransaction.create(
      wallet,
      counter,
      req.type(),
      req.amount(),
      req.includedInSettlement()
    );

    // 5. 저장 및 반환
    walletTransactionRepository.save(tx);
    return WalletTransactionResponseDto.from(tx);
  }

}