package com.hbbhbank.moamoa.external.service;

import com.hbbhbank.moamoa.external.client.HwanbeeExchangeClient;
import com.hbbhbank.moamoa.external.dto.request.ExchangeDealRequestDto;
import com.hbbhbank.moamoa.external.dto.request.ExchangeQuoteRequestDto;
import com.hbbhbank.moamoa.external.dto.response.ExchangeDealResponseDto;
import com.hbbhbank.moamoa.external.dto.response.ExchangeQuoteResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HwanbeeExchangeService {

  private final HwanbeeExchangeClient hwanbeeExchangeClient;

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
