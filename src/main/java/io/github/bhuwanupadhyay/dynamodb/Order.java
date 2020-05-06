package io.github.bhuwanupadhyay.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;

import java.util.Map;

@Data
@DynamoDBTable(tableName = "orders")
public class Order {
  @DynamoDBHashKey private String orderId;
  @DynamoDBAttribute private OrderLine orderLine;

  @DynamoDBAttribute
  @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
  private OrderStatus orderStatus;

  @DynamoDBAttribute
  private Map<String, String> additionalParams;

}
