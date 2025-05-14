package com.hbbhbank.moamoa.wallet.service;

import com.hbbhbank.moamoa.external.dto.response.GetVerificationCodeResponseDto;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.dto.request.CreateWalletRequestDto;
import com.hbbhbank.moamoa.wallet.dto.request.GetVerificationCodeWithinMoamoaRequestDto;
import com.hbbhbank.moamoa.wallet.dto.request.SearchWalletRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.GetWalletInfoResponseDto;
import com.hbbhbank.moamoa.wallet.dto.response.SearchWalletResponseDto;
import com.hbbhbank.moamoa.wallet.dto.response.CreateWalletResponseDto;

import java.util.List;

public interface WalletService {

  // 통화 코드를 통해 지갑 조회
  SearchWalletResponseDto getWalletByUserAndCurrency(SearchWalletRequestDto req);

  // 사용자 별 모든 지갑 목록 조회
  List<SearchWalletResponseDto> getAllWalletsByUser();

  // 인증 코드 요청
  GetVerificationCodeResponseDto getVerificationCode(GetVerificationCodeWithinMoamoaRequestDto req);

  // 지갑 생성
  CreateWalletResponseDto createWalletAfterVerification(CreateWalletRequestDto req);

  // 받는 사람 지갑 확인
  GetWalletInfoResponseDto getReceiverWalletInfo(String WalletNumber);

  // 지갑 번호로 지갑 조회
  Wallet getWalletByNumberOrThrow(String WalletNumber);
}
