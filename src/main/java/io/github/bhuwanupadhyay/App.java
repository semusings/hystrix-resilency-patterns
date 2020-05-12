package io.github.bhuwanupadhyay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableCircuitBreaker
@EnableRetry(proxyTargetClass = true)
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
