package com.hbbhbank.moamoa.external.client;

import com.hbbhbank.moamoa.external.dto.request.exchange.ExchangeDealRequestDto;
import com.hbbhbank.moamoa.external.dto.request.exchange.ExchangeQuoteRequestDto;
import com.hbbhbank.moamoa.external.dto.response.exchange.ExchangeDealResponseDto;
import com.hbbhbank.moamoa.external.dto.response.exchange.ExchangeQuoteResponseDto;

public interface HwanbeeExchangeClient {

  /**
   * 환비 API에 환율 견적(Quote)을 요청합니다.
   *
   * @param dto 환율 견적 요청 정보 (fromCurrency, toCurrency, amount 등 포함)
   * @return 환율 견적 응답 정보 (quoteId, 환율 등 포함)
   */
  ExchangeQuoteResponseDto requestExchangeQuote(ExchangeQuoteRequestDto dto);

  /**
   * 환비 API에 환전 체결(Deal)을 요청합니다.
   * <p>
   * 반드시 사전에 발급된 quoteId를 기반으로 요청해야 하며,
   * 멱등성 키(idempotencyKey)를 함께 전송하여 중복 거래를 방지합니다.
   *
   * @param dto 환전 체결 요청 정보 (quoteId, amount, idempotencyKey 포함)
   * @return 환전 체결 응답 정보 (체결 상태, 환전된 금액, 거래 시간 등 포함)
   */
  ExchangeDealResponseDto requestExchangeDeal(ExchangeDealRequestDto dto);
}
