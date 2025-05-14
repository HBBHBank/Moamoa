package com.hbbhbank.moamoa.wallet.service;

import com.hbbhbank.moamoa.external.dto.request.GenerateVerificationCodeRequestDto;
import com.hbbhbank.moamoa.external.dto.response.VerificationCodeResponseDto;
import com.hbbhbank.moamoa.wallet.dto.request.CreateWalletRequestDto;
import com.hbbhbank.moamoa.wallet.dto.request.WalletInquiryRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.WalletInquiryResponseDto;
import com.hbbhbank.moamoa.wallet.dto.response.WalletResponseDto;

import java.util.List;

public interface WalletService {

  // 지갑 조회
  WalletInquiryResponseDto showWallet(WalletInquiryRequestDto req);

  // 모든 지갑 목록 조회
  List<WalletInquiryResponseDto> getAllWalletsByUser();

  // 인증 코드 요청
  VerificationCodeResponseDto requestVerificationCode(GenerateVerificationCodeRequestDto req);

  // 지갑 생성
  WalletResponseDto createWallet(CreateWalletRequestDto req);
}
