package com.hbbhbank.moamoa.external.dto.request.account;

import com.hbbhbank.moamoa.wallet.dto.request.CreateWalletRequestDto;

// 지갑 생성 로직에서 필요한 외부 은행 계좌 인증 정보를 담고 있는 DTO
public record AccountVerificationContext(
  String externalAccountNumber, // 사용자가 입력한 외부 은행 계좌 번호
  String verificationCode, // 인증 코드
  String currencyCode // 지갑 생성 대상 통화 코드
) {
  public static AccountVerificationContext from(CreateWalletRequestDto dto) {
    return new AccountVerificationContext(
      dto.externalAccountNumber(),
      dto.verificationCode(),
      dto.currencyCode()
    );
  }
}
