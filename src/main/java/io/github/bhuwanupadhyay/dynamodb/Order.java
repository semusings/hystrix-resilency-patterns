package io.github.bhuwanupadhyay.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTyped;
import lombok.Data;

import java.util.Map;

@Data
// Declarative way to define table name
// @DynamoDBTable(tableName = "orders")
public class Order {
  @DynamoDBHashKey private String orderId;
  @DynamoDBAttribute private OrderLine orderLine;

  @DynamoDBAttribute
  @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
  private OrderStatus orderStatus;

  @DynamoDBAttribute private Map<String, String> additionalParams;
}
