package com.hbbhbank.moamoa.external.client;

import com.hbbhbank.moamoa.external.client.common.HwanbeeApiClient;
import com.hbbhbank.moamoa.external.client.common.HwanbeeApiEndpoints;
import com.hbbhbank.moamoa.external.dto.request.exchange.ExchangeDealRequestDto;
import com.hbbhbank.moamoa.external.dto.request.exchange.ExchangeQuoteRequestDto;
import com.hbbhbank.moamoa.external.dto.response.exchange.ExchangeDealResponseDto;
import com.hbbhbank.moamoa.external.dto.response.exchange.ExchangeQuoteResponseDto;
import com.hbbhbank.moamoa.external.exception.HwanbeeErrorCode;
import com.hbbhbank.moamoa.global.common.BaseResponse;
import com.hbbhbank.moamoa.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HwanbeeExchangeClientImpl implements HwanbeeExchangeClient {

  private final HwanbeeApiClient apiClient;
  private final HwanbeeApiEndpoints endpoints;

  @Override
  public ExchangeQuoteResponseDto requestExchangeQuote(ExchangeQuoteRequestDto dto) {
    BaseResponse<ExchangeQuoteResponseDto> response = apiClient.post(
      endpoints.getQuoteUrl(),
      dto,
      new ParameterizedTypeReference<>() {},
      HwanbeeErrorCode.EXCHANGE_QUOTE_FAILED
    );

    if (!"SUCCESS".equals(response.getResult().status())) {
      throw BaseException.type(HwanbeeErrorCode.EXCHANGE_QUOTE_FAILED);
    }

    return response.getResult();
  }

  @Override
  public ExchangeDealResponseDto requestExchangeDeal(ExchangeDealRequestDto dto) {
    BaseResponse<ExchangeDealResponseDto> response = apiClient.post(
      endpoints.getDealUrl(),
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