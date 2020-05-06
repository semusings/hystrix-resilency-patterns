package io.github.bhuwanupadhyay.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTyped;
import lombok.Data;

import java.util.Map;

@Data
@DynamoDBDocument
public class OrderItem {
  private String itemId;
  private Integer quantity;

	@DynamoDBAttribute
	@DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
	private OrderItemStatus itemStatus;

	@DynamoDBAttribute
	private Map<String, String> additionalParams;
}
