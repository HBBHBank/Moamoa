package com.hbbhbank.moamoa.external.client;

import com.hbbhbank.moamoa.external.common.HwanbeeApiEndpoints;
import com.hbbhbank.moamoa.external.dto.request.exchange.ExchangeDealRequestDto;
import com.hbbhbank.moamoa.external.dto.response.exchange.ExchangeDealResponseDto;
import com.hbbhbank.moamoa.external.dto.response.exchange.ExchangeRateResponseDto;
import com.hbbhbank.moamoa.external.dto.response.exchange.SingleExchangeRateResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class HwanbeeExchangeClientImpl implements HwanbeeExchangeClient {

  private final RestTemplate hwanbeeRestTemplate;
  private final HwanbeeApiEndpoints hwanbeeApiEndpoints;

  @Override
  public ExchangeRateResponseDto getAllExchangeRates(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<ExchangeRateResponseDto> response = hwanbeeRestTemplate.exchange(
      hwanbeeApiEndpoints.getExchangeRatesUrl(),
      HttpMethod.GET,
      request,
      ExchangeRateResponseDto.class
    );

    return response.getBody();
  }

  @Override
  public SingleExchangeRateResponseDto getExchangeRateByCurrency(String accessToken, String currencyCode) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    // 쿼리 파라미터 방식으로 URL 생성
    String url = UriComponentsBuilder
      .fromHttpUrl(hwanbeeApiEndpoints.getExchangeRateUrl()) // 예: http://localhost:8080/api/exchange/v1/rate
      .queryParam("currency", currencyCode)
      .toUriString();

    ResponseEntity<SingleExchangeRateResponseDto> response = hwanbeeRestTemplate.exchange(
      url,
      HttpMethod.GET,
      requestEntity,
      SingleExchangeRateResponseDto.class
    );

    return response.getBody();
  }

  @Override
  public ExchangeDealResponseDto requestExchange(String accessToken, ExchangeDealRequestDto request) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<ExchangeDealRequestDto> requestEntity = new HttpEntity<>(request, headers);

    ResponseEntity<ExchangeDealResponseDto> response = hwanbeeRestTemplate.exchange(
      hwanbeeApiEndpoints.getExchangeDealUrl(),
      HttpMethod.POST,
      requestEntity,
      ExchangeDealResponseDto.class
    );

    return response.getBody();
  }
}
