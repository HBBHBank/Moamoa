package com.hbbhbank.moamoa.external.service;

import com.hbbhbank.moamoa.external.client.HwanbeeAccountClientImpl;
import com.hbbhbank.moamoa.external.domain.UserAccountLink;
import com.hbbhbank.moamoa.external.dto.request.account.AccountVerificationContext;
import com.hbbhbank.moamoa.external.dto.request.account.GetVerificationCodeRequestDto;
import com.hbbhbank.moamoa.external.dto.request.account.VerificationCheckRequestDto;
import com.hbbhbank.moamoa.external.dto.response.account.VerificationCheckResponseDto;
import com.hbbhbank.moamoa.external.dto.response.account.GetVerificationCodeResponseDto;
import com.hbbhbank.moamoa.external.repository.HwanbeeLinkRepository;
import com.hbbhbank.moamoa.wallet.dto.request.GetVerificationCodeWithinMoamoaRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HwanbeeAccountService {

  private final HwanbeeAccountClientImpl hwanbeeAccountClient;
  private final HwanbeeLinkRepository hwanbeeLinkRepository;

  // 인증 코드 요청
  public GetVerificationCodeResponseDto getVerificationCodeFromHwanbee(Long userId, GetVerificationCodeWithinMoamoaRequestDto req) {
    GetVerificationCodeRequestDto enriched = GetVerificationCodeRequestDto.of(userId, req);
    return hwanbeeAccountClient.requestVerificationCode(enriched);
  }

  // 인증 검증 및 계좌 연결 정보 저장
  @Transactional
  public UserAccountLink verifyAndLinkAccountWithUser(Long userId, AccountVerificationContext req) {
    VerificationCheckRequestDto dto = VerificationCheckRequestDto.of(userId, req);
    VerificationCheckResponseDto resp = hwanbeeAccountClient.verifyDepositCode(dto);

    // 계좌 연결 정보 저장
    UserAccountLink link = UserAccountLink.create(
      userId,
      resp.externalBankAccountId(),
      req.externalAccountNumber(),
      req.currencyCode()
    );

    return hwanbeeLinkRepository.save(link);
  }
}
