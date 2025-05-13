package com.hbbhbank.moamoa.external.client;

import com.hbbhbank.moamoa.external.exception.HwanbeeErrorCode;
import com.hbbhbank.moamoa.global.common.BaseResponse;
import com.hbbhbank.moamoa.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class HwanbeeApiClient {

  @Qualifier("hwanbeeRestTemplate")
  private final RestTemplate restTemplate;

  // POST 요청
  public <T> T post(
    String url,
    Object body,
    ParameterizedTypeReference<BaseResponse<T>> typeRef,
    HwanbeeErrorCode failureCode
  ) {
    HttpEntity<Object> entity = createJsonEntity(body);

    try {
      ResponseEntity<BaseResponse<T>> response =
        restTemplate.exchange(url, HttpMethod.POST, entity, typeRef);

      validateResponse(response, failureCode);

      return response.getBody().getResult();

    } catch (RestClientException e) {
      log.warn("환비 API 호출 실패 [{}]: {}", url, e.getMessage(), e);
      throw BaseException.type(failureCode);
    }
  }

  // ----------------------
  // Private Helper Methods
  // ----------------------

  private HttpEntity<Object> createJsonEntity(Object body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(body, headers);
  }

  private <T> void validateResponse(ResponseEntity<BaseResponse<T>> response, HwanbeeErrorCode errorCode) {
    BaseResponse<T> body = response.getBody();

    boolean invalid = !response.getStatusCode().is2xxSuccessful()
      || body == null
      || body.getErrorCode() != null
      || body.getResult() == null;

    if (invalid) {
      log.warn("환비 API 응답 유효성 실패: status={}, body={}", response.getStatusCode(), body);
      throw BaseException.type(errorCode);
    }
  }
}
