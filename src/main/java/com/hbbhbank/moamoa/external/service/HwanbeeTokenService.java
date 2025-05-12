package com.hbbhbank.moamoa.external.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
// 환비의 JWT 토큰 발급 및 갱신
public class HwanbeeTokenService {

  private final RestTemplate restTemplate; // 동기식 클라이언트

  @Value("${hwanbee.client-id}")
  private String clientId;

  @Value("${hwanbee.client-secret}")
  private String clientSecret;

  @Value("${hwanbee.auth-url}")
  private String authUrl;

  private String accessToken;
  private LocalDateTime expiresAt;

  /**
   * 유효한 토큰 반환 (만료되었으면 자동 갱신)
   */
  public synchronized String getValidToken() {
    if (accessToken == null || LocalDateTime.now().isAfter(expiresAt)) {
      refreshToken(); // 만료되었으면 재발급
    }
    return accessToken; // 메모리에 캐싱된 기존 토큰 반환
  }

  /**
   * 토큰 갱신 로직 (동기 처리)
   */
  private void refreshToken() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    Map<String, String> body = Map.of(
      "client_id", clientId,
      "client_secret", clientSecret
    );

    HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

    ResponseEntity<Map> response = restTemplate.postForEntity(authUrl, request, Map.class);

    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new RuntimeException("환비 토큰 발급 실패");
    }

    this.accessToken = (String) response.getBody().get("access_token");
    int expiresInSec = (int) response.getBody().get("expires_in");

    this.expiresAt = LocalDateTime.now().plusSeconds(expiresInSec - 60); // 안전 마진 (만료 1분 전 새 토큰 발급)
  }
}

