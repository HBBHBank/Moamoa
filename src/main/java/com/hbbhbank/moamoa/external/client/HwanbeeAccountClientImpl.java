package com.hbbhbank.moamoa.external.client;

import com.hbbhbank.moamoa.external.client.common.HwanbeeApiClient;
import com.hbbhbank.moamoa.external.client.common.HwanbeeApiEndpoints;
import com.hbbhbank.moamoa.external.dto.request.account.VerificationCodeRequestDto;
import com.hbbhbank.moamoa.external.dto.request.account.VerificationCheckRequestDto;
import com.hbbhbank.moamoa.external.dto.response.account.VerificationCodeResponseDto;
import com.hbbhbank.moamoa.external.dto.response.account.VerificationCheckResponseDto;
import com.hbbhbank.moamoa.external.exception.HwanbeeErrorCode;
import com.hbbhbank.moamoa.global.common.BaseResponse;
import com.hbbhbank.moamoa.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HwanbeeAccountClientImpl implements HwanbeeAccountClient {

  private final HwanbeeApiClient apiClient;
  private final HwanbeeApiEndpoints endpoints;

  // 환비에 계좌인증 코드 요청 (1원 송금)
  @Override
  public VerificationCodeResponseDto requestVerificationCode(VerificationCodeRequestDto dto) {
    return apiClient.post(
      endpoints.getVerificationCodeUrl(),
      dto,
      new ParameterizedTypeReference<BaseResponse<VerificationCodeResponseDto>>() {},
      HwanbeeErrorCode.VERIFICATION_CODE_REQUEST_FAILED
    );
  }

  // 환비에 인증코드 검증 요청
  @Override
  public VerificationCheckResponseDto verifyInputCode(VerificationCheckRequestDto dto) {
    VerificationCheckResponseDto result = apiClient.post(
      endpoints.getVerificationCheckUrl(),
      dto,
      new ParameterizedTypeReference<BaseResponse<VerificationCheckResponseDto>>() {},
      HwanbeeErrorCode.VERIFICATION_CODE_CHECK_FAILED
    );
    return result;
  }
}