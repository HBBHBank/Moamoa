package com.hbbhbank.moamoa.external.auth;

import com.hbbhbank.moamoa.external.exception.HwanbeeErrorCode;
import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.user.domain.User;
import com.hbbhbank.moamoa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2TokenService {

  @Qualifier("tokenRestTemplate")
  private final RestTemplate tokenRestTemplate;
  private final UserRepository userRepository;

  @Value("${oauth2.client-id}")
  private String clientId;

  @Value("${oauth2.client-secret}")
  private String clientSecret;

  @Value("${oauth2.token-uri}")
  private String tokenUri;

  @Value("${oauth2.redirect-uri}")
  private String redirectUri;

  /**
   * 사용자의 환비 access token을 보장하는 메서드
   * 1. 유효한 access token이 있다면 → 그대로 반환
   * 2. 만료되었고 refresh token이 있다면 → refresh로 갱신
   * 3. 둘 다 안되면 → authorization code로 token 발급
   */
  public String ensureAccessToken(User user, String authorizationCode) {
    if (user.getHwanbeeAccessToken() != null && user.getHwanbeeTokenExpireAt().isAfter(LocalDateTime.now())) {
      return user.getHwanbeeAccessToken(); // 아직 유효한 access token이 있다면 재사용
    }

    if (user.getHwanbeeRefreshToken() != null) {
      try {
        return refreshAccessToken(user); // refresh로 access token 재발급
      } catch (BaseException e) {
        // refresh token이 만료되었거나 오류가 났다면 → 아래에서 authorization code로 재요청
      }
    }

    // access token도 없고 refresh도 실패했다면 → 처음처럼 authorization code로 요청
    return issueAccessTokenFromAuthorizationCode(user, authorizationCode);
  }

  /**
   * 1회성 인가 코드로 access + refresh token 발급
   */
  private String issueAccessTokenFromAuthorizationCode(User user, String code) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      headers.setBasicAuth(clientId, clientSecret);

      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.add("grant_type", "authorization_code");
      body.add("code", code);
      body.add("redirect_uri", redirectUri);

      HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
      ResponseEntity<Map> response = tokenRestTemplate.postForEntity(tokenUri, request, Map.class);

      return updateTokensFromResponse(user, response); // access/refresh token을 user에 저장
    } catch (RestClientException e) {
      throw new BaseException(HwanbeeErrorCode.TOKEN_REQUEST_FAILED);
    }
  }

  /**
   * 저장된 refresh token으로 access token 재발급
   */
  private String refreshAccessToken(User user) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      headers.setBasicAuth(clientId, clientSecret);

      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.add("grant_type", "refresh_token");
      body.add("refresh_token", user.getHwanbeeRefreshToken());

      HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
      ResponseEntity<Map> response = tokenRestTemplate.postForEntity(tokenUri, request, Map.class);

      return updateTokensFromResponse(user, response);
    } catch (RestClientException e) {
      throw new BaseException(HwanbeeErrorCode.TOKEN_REQUEST_FAILED);
    }
  }

  /**
   * 토큰 응답을 파싱하여 user 엔티티에 저장
   */
  private String updateTokensFromResponse(User user, ResponseEntity<Map> response) {
    if (!response.getStatusCode().is2xxSuccessful() || !response.hasBody()) {
      throw new BaseException(HwanbeeErrorCode.TOKEN_REQUEST_FAILED);
    }

    Map<String, Object> body = response.getBody();

    String accessToken = (String) body.get("access_token");
    String refreshToken = (String) body.get("refresh_token");
    Integer expiresIn = (Integer) body.get("expires_in");

    if (accessToken == null || refreshToken == null || expiresIn == null) {
      throw new BaseException(HwanbeeErrorCode.TOKEN_REQUEST_FAILED);
    }

    user.updateHwanbeeTokens(accessToken, refreshToken, expiresIn);
    userRepository.save(user);
    return accessToken;
  }
}

