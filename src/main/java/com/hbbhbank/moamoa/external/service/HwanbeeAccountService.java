package com.hbbhbank.moamoa.external.service;

import com.hbbhbank.moamoa.external.client.HwanbeeAccountClient;
import com.hbbhbank.moamoa.external.domain.UserAccountLink;
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

  /**
   * 인증 코드 발급 후 바로 반환
   */
  public VerificationCodeResponseDto generateVerificationCode(GenerateVerificationCodeRequestDto dto) {
    return hwanbeeAccountClient.requestVerificationCode(dto);
  }

  /**
   * 인증 코드 검사 후 성공 시 계좌 연결 정보 저장
   */
  @Transactional
  public UserAccountLink verifyAndLinkAccount(VerificationCheckRequestDto dto) {
    // 1) 환비 API 호출 → 성공하면 DTO 반환, 아니면 예외
    VerificationCheckResponseDto resp = hwanbeeAccountClient.verifyDepositCode(dto);

    // 2) 현재 로그인 유저 정보 얻기
    Long userId = SecurityUtil.getCurrentUserId();

    // 3) 저장할 엔티티 빌드
    UserAccountLink link = UserAccountLink.builder()
      .userId(userId)
      .externalBankAccountId(resp.externalBankAccountId())
      .externalBankAccountNumber(dto.externalBankAccountNumber())
      .build();

    // 4) DB 저장 후 반환
    return hwanbeeLinkRepository.save(link);
  }

}
