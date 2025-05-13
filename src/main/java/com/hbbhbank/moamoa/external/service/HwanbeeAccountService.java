package com.hbbhbank.moamoa.external.service;

import com.hbbhbank.moamoa.external.client.HwanbeeAccountClient;
import com.hbbhbank.moamoa.external.domain.UserAccountLink;
import com.hbbhbank.moamoa.external.dto.request.CreateVerificationContext;
import com.hbbhbank.moamoa.external.dto.request.GenerateVerificationCodeRequestDto;
import com.hbbhbank.moamoa.external.dto.request.VerificationCheckRequestDto;
import com.hbbhbank.moamoa.external.dto.response.VerificationCheckResponseDto;
import com.hbbhbank.moamoa.external.dto.response.VerificationCodeResponseDto;
import com.hbbhbank.moamoa.external.repository.HwanbeeLinkRepository;
import com.hbbhbank.moamoa.global.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HwanbeeAccountService {

  private final HwanbeeAccountClient hwanbeeAccountClient;
  private final HwanbeeLinkRepository hwanbeeLinkRepository;

  // 인증 코드 요청
  public VerificationCodeResponseDto requestVerificationCodeWithUser(GenerateVerificationCodeRequestDto req, Long userId) {
    GenerateVerificationCodeRequestDto enriched = GenerateVerificationCodeRequestDto.of(userId, req);
    return hwanbeeAccountClient.requestVerificationCode(enriched);
  }

  // 인증 검증 및 계좌 연결 정보 저장
  @Transactional
  public UserAccountLink verifyAndLinkAccountWithUser(Long userId, CreateVerificationContext req) {
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
