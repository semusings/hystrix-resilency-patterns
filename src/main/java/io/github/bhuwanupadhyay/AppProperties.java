package io.github.bhuwanupadhyay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "app")
@Component
public class AppProperties {

  private AwsProperties aws;

  private String inventoryBaseUrl;

  @Data
  public static class AwsProperties {
    private String region;
    private String queueUrl;
    private String tableName;
  }
}
