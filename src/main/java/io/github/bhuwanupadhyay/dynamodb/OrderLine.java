package io.github.bhuwanupadhyay.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import java.util.List;
import lombok.Data;

@Data
@DynamoDBDocument
public class OrderLine {
  @DynamoDBAttribute private List<OrderItem> orderItems;
}
