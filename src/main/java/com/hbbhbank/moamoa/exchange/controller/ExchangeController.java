package com.hbbhbank.moamoa.exchange.controller;

import com.hbbhbank.moamoa.exchange.service.ExchangeService;
import com.hbbhbank.moamoa.external.dto.request.exchange.ExchangeDealRequestDto;
import com.hbbhbank.moamoa.external.dto.response.exchange.ExchangeDealResponseDto;
import com.hbbhbank.moamoa.external.dto.response.exchange.ExchangeRateResponseDto;
import com.hbbhbank.moamoa.external.dto.response.exchange.SingleExchangeRateResponseDto;
import com.hbbhbank.moamoa.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/exchange")
@RequiredArgsConstructor
public class ExchangeController {

  private final ExchangeService exchangeService;

  /**
   * 전체 환율 정보 조회
   */
  @GetMapping("/rates")
  public ResponseEntity<BaseResponse<ExchangeRateResponseDto>> getAllRates() {
    return ResponseEntity.ok(BaseResponse.success(exchangeService.getAllExchangeRates()));
  }

  /**
   * 특정 통화의 환율 정보 조회
   */
  @GetMapping(value = "/rates", params = "currency")
  public ResponseEntity<BaseResponse<SingleExchangeRateResponseDto>> getRateByCurrency(
    @RequestParam String currency
  ) {
    return ResponseEntity.ok(BaseResponse.success(exchangeService.getExchangeRateByCurrency(currency)));
  }

  @PostMapping("/deal")
  public ResponseEntity<BaseResponse<ExchangeDealResponseDto>> requestExchange(
    @RequestBody ExchangeDealRequestDto requestDto
  ) {
    return ResponseEntity.ok(BaseResponse.success(exchangeService.requestExchange(requestDto)));
  }
}

