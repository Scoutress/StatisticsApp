package com.scoutress.KaimuxAdminStats.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

  @Bean
  @SuppressWarnings("unused")
  RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
