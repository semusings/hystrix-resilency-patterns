package io.github.bhuwanupadhyay;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppCfg {

  @Bean
  public AmazonSQS amazonSQS(AppProperties aws) {
    return AmazonSQSClientBuilder.standard().withRegion(aws.getAws().getRegion()).build();
  }

  @Bean
  public AmazonDynamoDB amazonDynamoDB(AppProperties aws) {
    return AmazonDynamoDBClientBuilder.standard().withRegion(aws.getAws().getRegion()).build();
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
