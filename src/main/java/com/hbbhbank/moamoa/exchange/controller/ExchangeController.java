package com.hbbhbank.moamoa.exchange.controller;

import com.hbbhbank.moamoa.exchange.dto.request.ExchangePointRequestDto;
import com.hbbhbank.moamoa.exchange.dto.response.ExchangePointResponseDto;
import com.hbbhbank.moamoa.exchange.service.ExchangeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exchange")
public class ExchangeController {

  private final ExchangeService exchangeService;

  @PostMapping
  public ResponseEntity<ExchangePointResponseDto> exchange(
    @Valid @RequestBody ExchangePointRequestDto requestDto
  ) {
    ExchangePointResponseDto response = exchangeService.exchange(requestDto);
    return ResponseEntity.ok(response);
  }
}

