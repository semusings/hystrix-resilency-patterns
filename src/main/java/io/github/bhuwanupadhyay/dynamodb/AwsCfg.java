package io.github.bhuwanupadhyay.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsCfg {

  @Bean
  public AmazonSQS amazonSQS(AppAws aws) {
    return AmazonSQSClientBuilder.standard().withRegion(aws.getRegion()).build();
  }

  @Bean
  public AmazonDynamoDB amazonDynamoDB(AppAws aws) {
    return AmazonDynamoDBClientBuilder.standard().withRegion(aws.getRegion()).build();
  }
}
