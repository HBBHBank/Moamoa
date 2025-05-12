package com.hbbhbank.moamoa.external.client;

import com.hbbhbank.moamoa.external.dto.request.GenerateVerificationCodeRequestDto;
import com.hbbhbank.moamoa.external.dto.request.VerificationCheckRequestDto;
import com.hbbhbank.moamoa.external.dto.response.VerificationCheckResponseDto;
import com.hbbhbank.moamoa.external.dto.response.VerificationCodeResponseDto;
import com.hbbhbank.moamoa.external.exception.HwanbeeErrorCode;
import com.hbbhbank.moamoa.global.common.BaseResponse;
import com.hbbhbank.moamoa.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HwanbeeAccountClient {

  private final HwanbeeApiClient apiClient;

  @Value("${hwanbee.verification-code-url}")
  private String verificationCodeUrl;

  @Value("${hwanbee.verification-check-url}")
  private String verificationCheckUrl;

  /**
   * 인증 코드 발급
   */
  public VerificationCodeResponseDto requestVerificationCode(GenerateVerificationCodeRequestDto dto) {
    BaseResponse<VerificationCodeResponseDto> base = apiClient.postForBaseResponse(
      verificationCodeUrl,
      dto,
      new ParameterizedTypeReference<BaseResponse<VerificationCodeResponseDto>>() {},
      HwanbeeErrorCode.VERIFICATION_CODE_REQUEST_FAILED
    );
    return base.getResult();
  }

  /**
   * 인증 코드 검증
   */
  public VerificationCheckResponseDto verifyDepositCode(VerificationCheckRequestDto dto) {
    BaseResponse<VerificationCheckResponseDto> base = apiClient.postForBaseResponse(
      verificationCheckUrl,
      dto,
      new ParameterizedTypeReference<BaseResponse<VerificationCheckResponseDto>>() {},
      HwanbeeErrorCode.VERIFICATION_CODE_CHECK_FAILED
    );

    VerificationCheckResponseDto resultDto = base.getResult();
    if (!"SUCCESS".equals(resultDto.result())) {
      throw BaseException.type(HwanbeeErrorCode.VERIFICATION_CODE_CHECK_FAILED);
    }
    return resultDto;
  }
}
