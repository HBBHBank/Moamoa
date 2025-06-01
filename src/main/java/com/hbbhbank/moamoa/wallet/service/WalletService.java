package com.hbbhbank.moamoa.wallet.service;

import com.hbbhbank.moamoa.external.dto.request.account.VerificationCodeRequestDto;
import com.hbbhbank.moamoa.wallet.domain.Wallet;
import com.hbbhbank.moamoa.wallet.dto.request.wallet.SearchWalletRequestDto;
import com.hbbhbank.moamoa.wallet.dto.response.wallet.BankAccountResponseDto;
import com.hbbhbank.moamoa.wallet.dto.response.wallet.CreateWalletResponseDto;
import com.hbbhbank.moamoa.wallet.dto.response.wallet.SearchWalletResponseDto;

import java.util.List;

public interface WalletService {

  // 환비에 계좌 인증코드 발급 요청
  void requestVerificationCode(VerificationCodeRequestDto req, String authorizationCode);

  // 환비에서 인증 완료 후 지갑 생성
  CreateWalletResponseDto createWalletAfterVerification(Integer inputCode);

  // 통화 코드를 통해 지갑 조회
  SearchWalletResponseDto getWalletByUserAndCurrency(SearchWalletRequestDto req);

  // 사용자 별 모든 지갑 목록 조회
  List<SearchWalletResponseDto> getAllWalletsByUser();

  // 지갑 번호로 지갑 조회
  Wallet getWalletByNumberOrThrow(String WalletNumber);

  // 환비 계좌 목록 조회
  List<BankAccountResponseDto> getBankAccountsByUser(String currencyCode);

  // 지갑 번호와 통화 코드로 지갑 조회 및 통화 검증
  Wallet getWalletByNumberAndVerifyCurrency(String walletNumber, String currencyCode);
}
