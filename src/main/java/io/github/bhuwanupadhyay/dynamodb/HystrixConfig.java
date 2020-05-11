package io.github.bhuwanupadhyay.dynamodb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HystrixConfig {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
