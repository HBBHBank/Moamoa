package com.hbbhbank.moamoa.external.config;

import com.hbbhbank.moamoa.external.client.HwanbeeAuthInterceptor;
import com.hbbhbank.moamoa.external.service.HwanbeeTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class RestTemplateConfig {

  @Bean
  public HwanbeeAuthInterceptor hwanbeeAuthInterceptor(@Lazy HwanbeeTokenService tokenService) {
    return new HwanbeeAuthInterceptor(tokenService);
  }

  @Bean(name = "hwanbeeRestTemplate")
  public RestTemplate hwanbeeRestTemplate(HwanbeeAuthInterceptor hwanbeeAuthInterceptor) {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setInterceptors(List.of(hwanbeeAuthInterceptor));
    return restTemplate;
  }

  @Bean(name = "tokenRestTemplate")
  public RestTemplate tokenRestTemplate() {
    return new RestTemplate();
  }
}

