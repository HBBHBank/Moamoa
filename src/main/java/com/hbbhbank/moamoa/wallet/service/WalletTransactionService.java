package com.hbbhbank.moamoa.wallet.service;

import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.wallet.domain.WalletTransaction;
import com.hbbhbank.moamoa.wallet.dto.request.CreateWalletTransactionRequestDto;
import com.hbbhbank.moamoa.wallet.dto.request.WalletInquiryRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.WalletTransactionResponseDto;
import com.hbbhbank.moamoa.wallet.exception.WalletErrorCode;
import com.hbbhbank.moamoa.wallet.repository.WalletTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WalletTransactionService {

  private final WalletTransactionRepository walletTransactionRepository;

  // 지갑 별 거래 내역 조회
  public WalletTransaction showWalletTransaction(WalletInquiryRequestDto req) {
    return walletTransactionRepository.findByWallet_Currency_Code(req.currencyCode())
      .orElseThrow(() -> BaseException.type(WalletErrorCode.NOT_FOUND_WALLET));
  }

  // 지갑 별 거래 내역 생성 (충전, 송금, 출금, 환전, 정산, 결제 시에 자동 생성)
  @Transactional
  public WalletTransactionResponseDto recordTransaction(CreateWalletTransactionRequestDto req) {
    WalletTransaction tx = WalletTransaction.builder()
      .wallet(req.wallet())
      .counterWallet(req.counterWallet())
      .type(req.type())
      .amount(req.amount())
      .includedInSettlement(req.includedInSettlement())
      .transactedAt(LocalDateTime.now())
      .build();

    walletTransactionRepository.save(tx);

    req.wallet().updateBalance(req.amount());

    return WalletTransactionResponseDto.from(tx);
  }
}
