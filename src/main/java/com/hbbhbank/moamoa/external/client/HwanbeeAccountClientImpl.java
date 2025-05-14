package com.hbbhbank.moamoa.external.client;

import com.hbbhbank.moamoa.external.client.common.HwanbeeApiClient;
import com.hbbhbank.moamoa.external.client.common.HwanbeeApiEndpoints;
import com.hbbhbank.moamoa.external.dto.request.account.GetVerificationCodeRequestDto;
import com.hbbhbank.moamoa.external.dto.request.account.VerificationCheckRequestDto;
import com.hbbhbank.moamoa.external.dto.response.account.GetVerificationCodeResponseDto;
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

  @Override
  public GetVerificationCodeResponseDto requestVerificationCode(GetVerificationCodeRequestDto dto) {
    return apiClient.post(
      endpoints.getVerificationCodeUrl(),
      dto,
      new ParameterizedTypeReference<BaseResponse<GetVerificationCodeResponseDto>>() {},
      HwanbeeErrorCode.VERIFICATION_CODE_REQUEST_FAILED
    );
  }

  @Override
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