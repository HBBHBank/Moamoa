package com.hbbhbank.moamoa.external.client;

import com.hbbhbank.moamoa.external.dto.request.GetVerificationCodeRequestDto;
import com.hbbhbank.moamoa.external.dto.request.VerificationCheckRequestDto;
import com.hbbhbank.moamoa.external.dto.response.VerificationCheckResponseDto;
import com.hbbhbank.moamoa.external.dto.response.GetVerificationCodeResponseDto;
import com.hbbhbank.moamoa.external.exception.HwanbeeErrorCode;
import com.hbbhbank.moamoa.global.common.BaseResponse;
import com.hbbhbank.moamoa.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HwanbeeAccountClient {

  private final HwanbeeApiClient apiClient;
  private final HwanbeeApiEndpoints endpoints;

  // 인증 코드 요청
  public GetVerificationCodeResponseDto requestVerificationCode(GetVerificationCodeRequestDto dto) {
    return apiClient.post(
      endpoints.getVerificationCodeUrl(),
      dto,
      new ParameterizedTypeReference<BaseResponse<GetVerificationCodeResponseDto>>() {},
      HwanbeeErrorCode.VERIFICATION_CODE_REQUEST_FAILED
    );
  }

  // 인증 코드 검증
  public VerificationCheckResponseDto verifyDepositCode(VerificationCheckRequestDto dto) {
    VerificationCheckResponseDto result = apiClient.post(
      endpoints.getVerificationCheckUrl(),
      dto,
      new ParameterizedTypeReference<BaseResponse<VerificationCheckResponseDto>>() {},
      HwanbeeErrorCode.VERIFICATION_CODE_CHECK_FAILED
    );

    if (!"SUCCESS".equals(result.result())) {
      throw BaseException.type(HwanbeeErrorCode.VERIFICATION_CODE_CHECK_FAILED);
    }
    return result;
  }
}