package com.hbbhbank.moamoa.exchange.service;

import com.hbbhbank.moamoa.external.client.HwanbeeExchangeClient;
import com.hbbhbank.moamoa.external.dto.request.exchange.ExchangeDealRequestDto;
import com.hbbhbank.moamoa.external.dto.response.exchange.ExchangeDealResponseDto;
import com.hbbhbank.moamoa.external.dto.response.exchange.ExchangeRateResponseDto;
import com.hbbhbank.moamoa.external.dto.response.exchange.SingleExchangeRateResponseDto;
import com.hbbhbank.moamoa.user.domain.User;
import com.hbbhbank.moamoa.user.repository.UserRepository;
import com.hbbhbank.moamoa.global.exception.BaseException;
import com.hbbhbank.moamoa.global.security.util.SecurityUtil;
import com.hbbhbank.moamoa.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

  private final HwanbeeExchangeClient hwanbeeExchangeClient;
  private final UserRepository userRepository;

  @Override
  public ExchangeRateResponseDto getAllExchangeRates() {
    String accessToken = getAccessTokenForCurrentUser();
    return hwanbeeExchangeClient.getAllExchangeRates(accessToken);
  }

  @Override
  public SingleExchangeRateResponseDto getExchangeRateByCurrency(String currencyCode) {
    String accessToken = getAccessTokenForCurrentUser();
    return hwanbeeExchangeClient.getExchangeRateByCurrency(accessToken, currencyCode);
  }

  @Override
  public ExchangeDealResponseDto requestExchange(ExchangeDealRequestDto request) {
    String accessToken = getAccessTokenForCurrentUser();
    return hwanbeeExchangeClient.requestExchange(accessToken, request);
  }

  /**
   * 현재 로그인된 유저의 환비 API 액세스 토큰을 가져옵니다.
   */
  private String getAccessTokenForCurrentUser() {
    Long userId = SecurityUtil.getCurrentUserId(); // 로그인된 유저 ID 가져오기
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new BaseException(UserErrorCode.USER_NOT_FOUND));

    String accessToken = user.getAccessToken(); // User 엔티티에 저장된 환비 Access Token 가져오기
    if (accessToken == null || accessToken.isBlank()) {
      throw new BaseException(UserErrorCode.HWANBEE_TOKEN_NOT_FOUND);
    }

    return accessToken;
  }
}
