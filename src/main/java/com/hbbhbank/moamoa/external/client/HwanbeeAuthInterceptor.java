package com.hbbhbank.moamoa.external.client;

import com.hbbhbank.moamoa.external.service.HwanbeeTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@RequiredArgsConstructor
// 환비에게 요청을 보낼때마다 JWT 토큰을 헤더에 추가
public class HwanbeeAuthInterceptor implements ClientHttpRequestInterceptor {

  private final HwanbeeTokenService tokenService;

  @Override
  public ClientHttpResponse intercept(
    HttpRequest request, byte[] body, ClientHttpRequestExecution execution
  ) throws IOException {
    String token = tokenService.getValidToken();
    request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    return execution.execute(request, body);
  }
}

