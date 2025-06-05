package com.hbbhbank.moamoa.exchange.service;

import com.hbbhbank.moamoa.external.dto.request.exchange.ExchangeDealRequestDto;
import com.hbbhbank.moamoa.external.dto.response.exchange.ExchangeDealResponseDto;
import com.hbbhbank.moamoa.external.dto.response.exchange.ExchangeRateResponseDto;
import com.hbbhbank.moamoa.external.dto.response.exchange.SingleExchangeRateResponseDto;

public interface ExchangeService {

  /**
   * 모든 환율 정보를 조회합니다.
   */
  ExchangeRateResponseDto getAllExchangeRates();

  /**
   * 특정 통화의 환율 정보를 조회합니다.
   */
  SingleExchangeRateResponseDto getExchangeRateByCurrency(String currencyCode);

  /**
   * 환전 요청을 처리합니다.
   */
  ExchangeDealResponseDto requestExchange(ExchangeDealRequestDto request);
}

