package com.hbbhbank.moamoa.wallet.service;

import com.hbbhbank.moamoa.wallet.dto.request.CreateWalletTransactionRequestDto;
import com.hbbhbank.moamoa.wallet.dto.request.WalletInquiryRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.CreateWalletTransactionResponseDto;
import com.hbbhbank.moamoa.wallet.domain.WalletTransaction;

public interface WalletTransactionService {

  // 지갑 거래 내역 조회
  WalletTransaction showWalletTransaction(WalletInquiryRequestDto req);

  // 모든 지갑 거래 내역 조회
  CreateWalletTransactionResponseDto recordTransaction(CreateWalletTransactionRequestDto req);
}
