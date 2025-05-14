package com.hbbhbank.moamoa.external.service;

import com.hbbhbank.moamoa.external.client.HwanbeeExchangeClientImpl;
import com.hbbhbank.moamoa.external.dto.request.exchange.ExchangeDealRequestDto;
import com.hbbhbank.moamoa.external.dto.request.exchange.ExchangeQuoteRequestDto;
import com.hbbhbank.moamoa.external.dto.response.exchange.ExchangeDealResponseDto;
import com.hbbhbank.moamoa.external.dto.response.exchange.ExchangeQuoteResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HwanbeeExchangeService {

  private final HwanbeeExchangeClientImpl hwanbeeExchangeClient;

  // 환율 조회
  public ExchangeQuoteResponseDto showExchangeRate(ExchangeQuoteRequestDto request) {
    ExchangeQuoteResponseDto responseDto = hwanbeeExchangeClient.requestExchangeQuote(request);
    return responseDto;
  }

  // 환전
  public ExchangeDealResponseDto exchange(ExchangeDealRequestDto request) {
    ExchangeDealResponseDto responseDto = hwanbeeExchangeClient.requestExchangeDeal(request);
    return responseDto;
  }
}
