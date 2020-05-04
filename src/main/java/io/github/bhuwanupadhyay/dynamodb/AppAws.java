package io.github.bhuwanupadhyay.dynamodb;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "app.aws")
@Component
public class AppAws {

  private String region;
  private String queueUrl;
}
