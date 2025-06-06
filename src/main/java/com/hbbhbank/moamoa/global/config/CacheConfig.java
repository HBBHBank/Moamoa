// CacheConfig.java
package com.hbbhbank.moamoa.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbbhbank.moamoa.external.dto.response.exchange.ExchangeRateResponseDto;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
    // 기본 ObjectMapper 사용 (타입 정보 없이)
    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
      .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
      .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
        new Jackson2JsonRedisSerializer<>(ExchangeRateResponseDto.class)
      ));

    Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
    configMap.put("exchangeRates", config.entryTtl(Duration.ofHours(24)));

    return RedisCacheManager.builder(connectionFactory)
      .cacheDefaults(config)
      .withInitialCacheConfigurations(configMap)
      .build();
  }
}