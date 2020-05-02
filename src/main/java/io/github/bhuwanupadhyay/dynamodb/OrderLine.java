package io.github.bhuwanupadhyay.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.Data;

import java.util.List;

@Data
@DynamoDBDocument
public class OrderLine {
  @DynamoDBAttribute private List<OrderItem> orderItems;
}
