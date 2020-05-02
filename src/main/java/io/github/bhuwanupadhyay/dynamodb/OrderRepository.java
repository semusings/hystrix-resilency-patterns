package io.github.bhuwanupadhyay.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.stereotype.Component;

@Component
public class OrderRepository {

  private final DynamoDBMapper dbMapper;

  public OrderRepository(AmazonDynamoDB dynamoDB) {
    this.dbMapper = new DynamoDBMapper(dynamoDB);
  }

  public void save(Order order) {
    this.dbMapper.save(order);
  }
}
