package io.github.bhuwanupadhyay.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderService {
  public static final String REGION = "us-east-1";
  private final ObjectMapper objectMapper;
  private final DynamoDBMapper dynamoDBMapper;
  private final AmazonSQS amazonSQS;
  private final AppAws aws;

  public OrderService(AmazonDynamoDB dynamoDB, AmazonSQS amazonSQS, AppAws aws) {
    this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    this.dynamoDBMapper = new DynamoDBMapper(dynamoDB);
    this.amazonSQS = amazonSQS;
    this.aws = aws;
  }

  @HystrixCommand(fallbackMethod = "sendToQueue")
  public void createOrder(final Order order) {
    log.info("BEGIN-----------#placeOrder");
    log.info("Saving order");
    try {
      dynamoDBMapper.save(order);
      log.info("Successfully saved order.");
    } catch (Exception e) {
      log.error("Error on saving order.", e);
      throw e;
    } finally {
      log.info("END-----------#placeOrder");
    }
  }

  public void sendToQueue(Order order) {
    log.info("BEGIN-----------#sendToQueue");
    log.info("On failure publishing order to dead later queue");
    try {
      final SendMessageRequest request =
          new SendMessageRequest()
              .withMessageBody(asString(order))
              .withQueueUrl("https://sqs.us-east-1.amazonaws.com/346901423380/orders");
      amazonSQS.sendMessage(request);
      log.info("Successfully published order.");
    } catch (Exception e) {
      log.info("Error on publishing order [{}]", ExceptionUtils.getStackTrace(e));
      throw e;
    } finally {
      log.info("END-----------#sendToQueue");
    }
  }

  @SneakyThrows
  private String asString(Order order) {
    return objectMapper.writeValueAsString(order);
  }
}
