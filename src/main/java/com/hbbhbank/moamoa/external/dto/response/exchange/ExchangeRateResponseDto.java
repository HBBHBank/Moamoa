package com.hbbhbank.moamoa.external.dto.response.exchange;

import java.util.List;

public record ExchangeRateResponseDto(
  int status,
  String message,
  List<ExchangeRateDataDto> data
) {
}
