package com.hbbhbank.moamoa.external.client;

import com.hbbhbank.moamoa.external.exception.HwanbeeErrorCode;
import com.hbbhbank.moamoa.global.common.BaseResponse;
import com.hbbhbank.moamoa.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class HwanbeeApiClient {

  @Qualifier("hwanbeeRestTemplate")
  private final RestTemplate restTemplate;

  public <T> BaseResponse<T> postForBaseResponse(
    String url,
    Object requestDto,
    ParameterizedTypeReference<BaseResponse<T>> typeRef,
    HwanbeeErrorCode failureCode
  ) {
    // 1) JSON 헤더 준비
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    // 2) HttpEntity 에 DTO + 헤더 담기
    HttpEntity<Object> request = new HttpEntity<>(requestDto, headers);

    ResponseEntity<BaseResponse<T>> response;
    try {
      // 3) 환비 API 호출
      response = restTemplate.exchange(url, HttpMethod.POST, request, typeRef);
    } catch (RestClientException e) {
      // 네트워크 에러·타임아웃 등
      throw BaseException.type(failureCode);
    }

    BaseResponse<T> body = response.getBody();
    // 4) HTTP 상태, errorCode, result null 여부 전부 검사
    if (!response.getStatusCode().is2xxSuccessful()
      || body == null
      || body.getErrorCode() != null
      || body.getResult() == null
    ) {
      throw BaseException.type(failureCode);
    }

    // 5) 성공한 BaseResponse 객체 반환
    return body;
  }
}
