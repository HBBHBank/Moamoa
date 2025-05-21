package com.hbbhbank.moamoa.wallet.service;

import com.hbbhbank.moamoa.external.dto.request.account.VerificationCodeRequestDto;
import com.hbbhbank.moamoa.external.dto.response.account.VerificationCodeResponseDto;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.dto.request.wallet.CreateWalletRequestDto;
import com.hbbhbank.moamoa.wallet.dto.request.wallet.SearchWalletRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.wallet.CreateWalletResponseDto;
import com.hbbhbank.moamoa.wallet.dto.response.wallet.GetWalletInfoResponseDto;
import com.hbbhbank.moamoa.wallet.dto.response.wallet.SearchWalletResponseDto;

import java.util.List;

public interface WalletService {

  // 환비에 계좌 인증코드 발급 요청
  void requestVerificationCode(VerificationCodeRequestDto req);

  // 환비에서 인증 완료 후 지갑 생성
  CreateWalletResponseDto createWalletAfterVerification(Integer inputCode);

  // 통화 코드를 통해 지갑 조회
  SearchWalletResponseDto getWalletByUserAndCurrency(SearchWalletRequestDto req);

  // 사용자 별 모든 지갑 목록 조회
  List<SearchWalletResponseDto> getAllWalletsByUser();

  // 받는 사람 지갑 확인
  GetWalletInfoResponseDto getReceiverWalletInfo(String WalletNumber);

  // 지갑 번호로 지갑 조회
  Wallet getWalletByNumberOrThrow(String WalletNumber);
}
