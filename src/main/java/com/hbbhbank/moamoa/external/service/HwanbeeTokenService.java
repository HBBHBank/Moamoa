package com.hbbhbank.moamoa.external.service;

import com.hbbhbank.moamoa.external.exception.HwanbeeErrorCode;
import com.hbbhbank.moamoa.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class HwanbeeTokenService {

  @Qualifier("tokenRestTemplate")
  private final RestTemplate restTemplate;

  public HwanbeeTokenService(@Qualifier("tokenRestTemplate") RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Value("${hwanbee.client-id}")
  private String clientId;

  @Value("${hwanbee.client-secret}")
  private String clientSecret;

  @Value("${hwanbee.auth-url}")
  private String authUrl;

  private String accessToken;
  private LocalDateTime expiresAt;

  public synchronized String getValidToken() {
    if (accessToken == null || LocalDateTime.now().isAfter(expiresAt)) {
      refreshToken();
    }
    return accessToken;
  }

  private void refreshToken() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    Map<String, String> body = Map.of(
      "client_id", clientId,
      "client_secret", clientSecret
    );

    HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
    ResponseEntity<Map> response = restTemplate.postForEntity(authUrl, request, Map.class);

    if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
      throw BaseException.type(HwanbeeErrorCode.TOKEN_REQUEST_FAILED);
    }

    this.accessToken = (String) response.getBody().get("access_token");
    int expiresInSec = (int) response.getBody().get("expires_in");
    this.expiresAt = LocalDateTime.now().plusSeconds(expiresInSec - 60);
  }

}
