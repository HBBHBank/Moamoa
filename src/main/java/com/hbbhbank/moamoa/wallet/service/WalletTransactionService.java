package com.hbbhbank.moamoa.wallet.service;

import com.hbbhbank.moamoa.wallet.domain.WalletTransaction;
import com.hbbhbank.moamoa.wallet.domain.WalletTransactionType;
import com.hbbhbank.moamoa.wallet.dto.request.CreateTransactionRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.CreateWalletTransactionResponseDto;

import java.util.List;

public interface WalletTransactionService {

  // 지갑 별 거래 내역 최근 1건 조회
  WalletTransaction showWalletTransaction(String currencyCode);

  // 지갑 별 거래 내역 리스트 조회
  List<WalletTransaction> getAllTransactionsByWallet(String currencyCode);

  // 지갑 별 거래 타입에 따른 거래 내역 조회
  List<WalletTransaction> getTransactionsByWalletAndType(String currencyCode, WalletTransactionType type);

  // 거래 내역 생성
  CreateWalletTransactionResponseDto recordTransaction(CreateTransactionRequestDto req);
}
