package com.hbbhbank.moamoa.external.client.common;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class HwanbeeApiEndpoints {

  @Value("${hwanbee.verification-code-url}")
  private String verificationCodeUrl;

  @Value("${hwanbee.verification-check-url}")
  private String verificationCheckUrl;

  @Value("${hwanbee.auth-url}")
  private String authUrl;

  @Value("${hwanbee.transfer-url}")
  private String transferUrl;

  @Value("${hwanbee.quote-url}")
  private String quoteUrl;

  @Value("${hwanbee.deal-url}")
  private String dealUrl;
}