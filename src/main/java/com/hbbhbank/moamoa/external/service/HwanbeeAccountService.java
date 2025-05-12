package com.hbbhbank.moamoa.external.service;

import com.hbbhbank.moamoa.external.client.HwanbeeAccountClient;
import com.hbbhbank.moamoa.external.domain.UserAccountLink;
import com.hbbhbank.moamoa.external.dto.request.VerificationCheckRequestDto;
import com.hbbhbank.moamoa.external.dto.request.VerificationCodeRequestDto;
import com.hbbhbank.moamoa.external.dto.response.VerificationCodeResponseDto;
import com.hbbhbank.moamoa.external.exception.HwanbeeErrorCode;
import com.hbbhbank.moamoa.external.repository.HwanbeeLinkRepository;
import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 계좌 인증 및 연결 비즈니스 로직 담당 서비스
 */
@Service
@RequiredArgsConstructor
public class HwanbeeAccountService {

  private final HwanbeeAccountClient hwanbeeAccountClient;
  private final HwanbeeLinkRepository hwanbeeLinkRepository;

  /**
   * 인증 코드 발급 요청 후 사용자에게 반환
   */
  public String generateVerificationCode(User user, String currency, String accountNumber) {
    VerificationCodeRequestDto requestDto = new VerificationCodeRequestDto(user.getId(), currency, accountNumber);
    VerificationCodeResponseDto responseDto = hwanbeeAccountClient.requestVerificationCode(requestDto);
    return responseDto.verificationCode();
  }


  /**
   * 인증 코드 검증 후 계좌 정보를 DB에 저장
   */
  @Transactional
  public UserAccountLink verifyAndLinkAccount(User user, String accountNumber, String verificationCode) {
    VerificationCheckRequestDto requestDto = new VerificationCheckRequestDto(user.getId(), accountNumber, verificationCode);
    boolean verified = hwanbeeAccountClient.verifyDepositCode(requestDto);

    if (!verified) {
      throw BaseException.type(HwanbeeErrorCode.ACCOUNT_VERIFICATION_FAILED);
    }

    UserAccountLink link = UserAccountLink.builder()
      .user(user)
      .externalBankAccountId("HWB-FOREIGN")
      .externalBankAccountNumber(accountNumber)
      .build();

    return hwanbeeLinkRepository.save(link);
  }
}