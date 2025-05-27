package com.hbbhbank.moamoa.external.common;

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

  @Value("${hwanbee.remittance-url}")
  private String remittanceUrl;
}