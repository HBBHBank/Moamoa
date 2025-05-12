package com.hbbhbank.moamoa.external.client;

import com.hbbhbank.moamoa.external.dto.request.ExchangeDealRequestDto;
import com.hbbhbank.moamoa.external.dto.request.ExchangeQuoteRequestDto;
import com.hbbhbank.moamoa.external.dto.response.ExchangeDealResponseDto;
import com.hbbhbank.moamoa.external.dto.response.ExchangeQuoteResponseDto;
import com.hbbhbank.moamoa.external.exception.HwanbeeErrorCode;
import com.hbbhbank.moamoa.global.common.BaseResponse;
import com.hbbhbank.moamoa.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HwanbeeExchangeClient {

  private final HwanbeeApiClient apiClient;

  @Value("https://api.hwanbee.com/fx/quote")
  private String quoteUrl;

  @Value("https://api.hwanbee.com/fx/deal")
  private String dealUrl;

  // 환비 API 환율 견적 조회
  public ExchangeQuoteResponseDto requestExchangeQuote(ExchangeQuoteRequestDto dto) {
    BaseResponse<ExchangeQuoteResponseDto> response = apiClient.postForBaseResponse(
      quoteUrl,
      dto,
      new ParameterizedTypeReference<>() {},
      HwanbeeErrorCode.EXCHANGE_QUOTE_FAILED
    );

    if (!"SUCCESS".equals(response.getResult().status())) {
      throw BaseException.type(HwanbeeErrorCode.EXCHANGE_QUOTE_FAILED);
    }

    return response.getResult();
  }

  // 환비 API 환전 진행
  public ExchangeDealResponseDto requestExchangeDeal(ExchangeDealRequestDto dto) {
    BaseResponse<ExchangeDealResponseDto> response = apiClient.postForBaseResponse(
      dealUrl,
      dto,
      new ParameterizedTypeReference<>() {},
      HwanbeeErrorCode.EXCHANGE_DEAL_FAILED
    );

    if (!"SUCCESS".equals(response.getResult().status())) {
      throw BaseException.type(HwanbeeErrorCode.EXCHANGE_DEAL_FAILED);
    }

    return response.getResult();
  }
}