package com.hbbhbank.moamoa.external.client.common;

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

  /**
   * 환비 API에 POST 요청을 보내고, 응답을 공통 포맷으로 파싱해 결과 데이터를 반환합니다.
   *
   * @param url         요청할 Hwanbee API URL
   * @param body        요청 본문 (보낼 DTO 객체)
   * @param typeRef     응답 제네릭 타입 참조 (BaseResponse<T>)
   * @param failureCode 실패 시 반환할 HwanbeeErrorCode
   * @param <T>         결과 데이터 타입
   * @return 응답의 result 필드 값 (BaseResponse<T> 중 T)
   * @throws BaseException 요청 실패 또는 응답 유효성 실패 시 발생
   */
  public <T> T post(
    String url, // 호출할 Hwanbee API URL
    Object body, // 요청 본문 객체 (Request DTO)
    ParameterizedTypeReference<BaseResponse<T>> typeRef, // 응답 타입(BaseResponse<T>)을 정확하게 파악하기 위한 타입레퍼런스
    HwanbeeErrorCode failureCode // 실패 시 반환할 에러 코드 (HwanbeeErrorCode)
  ) {
    HttpEntity<Object> entity = createJsonEntity(body); // body를 포함한 HTTP 요청 객체 생성

    try {
      // HTTP 요청 실행
      ResponseEntity<BaseResponse<T>> response =
        restTemplate.exchange(url, HttpMethod.POST, entity, typeRef);

      // 응답 유효성 검증
      validateResponse(response, failureCode);

      // 정상 결과 반환
      return response.getBody().getResult();

    } catch (RestClientException e) { // 실패 시 예외 처리
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
