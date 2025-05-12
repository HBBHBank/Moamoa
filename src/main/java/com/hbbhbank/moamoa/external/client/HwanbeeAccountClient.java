package com.hbbhbank.moamoa.external.client;

import com.hbbhbank.moamoa.external.dto.request.VerificationCheckRequestDto;
import com.hbbhbank.moamoa.external.dto.request.VerificationCodeRequestDto;
import com.hbbhbank.moamoa.external.dto.response.VerificationCodeResponseDto;
import com.hbbhbank.moamoa.external.exception.HwanbeeErrorCode;
import com.hbbhbank.moamoa.global.common.BaseResponse;
import com.hbbhbank.moamoa.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class HwanbeeAccountClient {

  private final RestTemplate restTemplate;

  @Value("${hwanbee.verification-code-url}")
  private String verificationCodeUrl;

  @Value("${hwanbee.verification-check-url}")
  private String verificationCheckUrl;

  public VerificationCodeResponseDto requestVerificationCode(VerificationCodeRequestDto dto) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<VerificationCodeRequestDto> request = new HttpEntity<>(dto, headers);

    ResponseEntity<BaseResponse> response = restTemplate.exchange(
      verificationCodeUrl,
      HttpMethod.POST,
      request,
      BaseResponse.class
    );

    if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || response.getBody().getResult() == null) {
      throw BaseException.type(HwanbeeErrorCode.VERIFICATION_CODE_REQUEST_FAILED);
    }

    return (VerificationCodeResponseDto) response.getBody().getResult();
  }

  public boolean verifyDepositCode(VerificationCheckRequestDto dto) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<VerificationCheckRequestDto> request = new HttpEntity<>(dto, headers);

    ResponseEntity<BaseResponse> response = restTemplate.exchange(
      verificationCheckUrl,
      HttpMethod.POST,
      request,
      BaseResponse.class
    );

    if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
      throw BaseException.type(HwanbeeErrorCode.VERIFICATION_CODE_CHECK_FAILED);
    }

    String result = (String) response.getBody().getResult();
    return "SUCCESS".equals(result);
  }
}
