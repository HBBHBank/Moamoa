package com.hbbhbank.moamoa.wallet.service;

import com.hbbhbank.moamoa.external.dto.response.GetVerificationCodeResponseDto;
import com.hbbhbank.moamoa.wallet.dto.request.CreateWalletRequestDto;
import com.hbbhbank.moamoa.wallet.dto.request.GetVerificationCodeWithinMoamoaRequestDto;
import com.hbbhbank.moamoa.wallet.dto.request.WalletInquiryRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.WalletInquiryResponseDto;
import com.hbbhbank.moamoa.wallet.dto.response.CreateWalletResponseDto;

import java.util.List;

public interface WalletService {

  // 통화 코드를 통해 지갑 조회
  WalletInquiryResponseDto getWalletByUserAndCurrency(WalletInquiryRequestDto req);

  // 사용자 별 모든 지갑 목록 조회
  List<WalletInquiryResponseDto> getAllWalletsByUser();

  // 인증 코드 요청
  GetVerificationCodeResponseDto getVerificationCode(GetVerificationCodeWithinMoamoaRequestDto req);

  // 지갑 생성
  CreateWalletResponseDto createWalletAfterVerification(CreateWalletRequestDto req);
}
