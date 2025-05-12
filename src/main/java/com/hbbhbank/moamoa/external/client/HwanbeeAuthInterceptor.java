package com.hbbhbank.moamoa.external.client;

import com.hbbhbank.moamoa.external.service.HwanbeeTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
@RequiredArgsConstructor
// 환비에게 요청을 보낼때마다 JWT 토큰을 헤더에 추가
public class HwanbeeAuthInterceptor implements ClientHttpRequestInterceptor {

  private final HwanbeeTokenService tokenService;

  /**
   * HTTP 요청 가로채기 - Authorization 헤더에 토큰 추가
   */
  @Override
  public ClientHttpResponse intercept(
    HttpRequest request, byte[] body, ClientHttpRequestExecution execution
  ) throws IOException {

    String token = tokenService.getValidToken(); // 유효한 토큰 조회
    request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token); // 헤더에 추가
    return execution.execute(request, body); // 요청 실행
  }

  /**
   * RestTemplate Bean 등록
   * - 위 인터셉터를 적용한 인스턴스 반환
   */
  @Bean
  public RestTemplate restTemplate(HwanbeeAuthInterceptor interceptor) {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.getInterceptors().add(interceptor);
    return restTemplate;
  }

}

