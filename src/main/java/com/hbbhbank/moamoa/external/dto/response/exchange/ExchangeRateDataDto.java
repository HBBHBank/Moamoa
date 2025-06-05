package com.hbbhbank.moamoa.external.dto.response.exchange;

public record ExchangeRateDataDto(
  String currency,
  String registrationTime,
  String bankOfKoreaRate
) {
}
