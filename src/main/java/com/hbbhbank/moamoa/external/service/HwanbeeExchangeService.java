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

  public ExchangeQuoteResponseDto showExchangeRate(ExchangeQuoteRequestDto request) {
    ExchangeQuoteResponseDto responseDto = hwanbeeExchangeClient.requestExchangeQuote(request);
    return responseDto;
  }

  public ExchangeDealResponseDto exchange(ExchangeDealRequestDto request) {
    ExchangeDealResponseDto responseDto = hwanbeeExchangeClient.requestExchangeDeal(request);
    return responseDto;
  }
}
