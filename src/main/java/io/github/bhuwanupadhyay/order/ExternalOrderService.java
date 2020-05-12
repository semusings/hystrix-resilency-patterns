package io.github.bhuwanupadhyay.order;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.github.bhuwanupadhyay.AppProperties;
import io.github.bhuwanupadhyay.AppProperties.AwsProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExternalOrderService {
  private final ObjectMapper objectMapper;
  private final DynamoDBMapper dynamoDBMapper;
  private final AmazonSQS amazonSQS;
  private final AwsProperties aws;

  public ExternalOrderService(
      AmazonDynamoDB dynamoDB, AmazonSQS amazonSQS, AppProperties appProperties) {
    this.amazonSQS = amazonSQS;
    this.aws = appProperties.getAws();
    this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    this.dynamoDBMapper =
        new DynamoDBMapper(
            dynamoDB,
            DynamoDBMapperConfig.builder()
                // Reading table name from configurations
                .withTableNameResolver((clazz, config) -> this.aws.getTableName())
                .build());
  }

  @HystrixCommand(commandKey = "createOrderCommand", fallbackMethod = "sendToQueue")
  public void createOrder(final Order order) {
    log.info("BEGIN-----------#createOrder()");
    log.info("Saving order");
    try {
      dynamoDBMapper.save(order);
      log.info("Successfully saved order.");
    } catch (Exception e) {
      log.error("Error on saving order.", e);
      throw e;
    } finally {
      log.info("END-----------#createOrder()");
    }
  }

  /** The fallback method needs to have same return type and parameters. */
  @Retryable(maxAttempts = 4)
  public void sendToQueue(Order order) {
    log.info("BEGIN-----------#sendToQueue()");
    log.info("On failure publishing order to dead later queue");
    try {
      final SendMessageRequest request =
          new SendMessageRequest().withMessageBody(asString(order)).withQueueUrl(aws.getQueueUrl());
      amazonSQS.sendMessage(request);
      log.info("Successfully published order.");
    } catch (Exception e) {
      log.error("Error on publishing order", e);
      throw e;
    } finally {
      log.info("END-----------#sendToQueue()");
    }
  }

  /** The recover method needs to have same return type and parameters. */
  @Recover
  public void notifyAdmin(Order order) {
    log.error("System not able handle create order request: {}", order);
    log.info("Successfully notified to user.");
  }

  @SneakyThrows
  private String asString(Order order) {
    return objectMapper.writeValueAsString(order);
  }
}
