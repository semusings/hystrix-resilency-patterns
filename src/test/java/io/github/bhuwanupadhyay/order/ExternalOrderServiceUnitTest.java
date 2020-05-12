package io.github.bhuwanupadhyay.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ExternalOrderServiceUnitTest {

  @Autowired private ExternalOrderService externalOrderService;

  @MockBean private AmazonDynamoDB dynamoDB;
  @MockBean private AmazonSQS amazonSQS;

  @BeforeEach
  void setUp() {}

  @Test
  void canPlaceOrderSuccessfully() {
    Order order = newOrder();
    order.setOrderId(UUID.randomUUID().toString());
    externalOrderService.createOrder(order);
    verify(dynamoDB).updateItem(any(UpdateItemRequest.class));
  }

  @Test
  void whenNotPlaceOrderThenWriteToDLQ() {
    when(dynamoDB.updateItem(any(UpdateItemRequest.class))).thenThrow(new RuntimeException(""));
    Order order = newOrder();
    externalOrderService.createOrder(order);
    verify(amazonSQS).sendMessage(any(SendMessageRequest.class));
  }

  @Test
  void whenNotPublishedThenRetryFourTimeTimes() {
    when(dynamoDB.updateItem(any(UpdateItemRequest.class))).thenThrow(new RuntimeException(""));
    when(amazonSQS.sendMessage(any(SendMessageRequest.class))).thenThrow(new RuntimeException(""));
    Order order = newOrder();
    externalOrderService.sendToQueue(order);
    verify(amazonSQS, times(4)).sendMessage(any(SendMessageRequest.class));
  }

  private Order newOrder() {
    final Order order = new Order();
    final OrderLine orderLine = new OrderLine();

    final List<OrderItem> orderItems = new ArrayList<>();
    final HashMap<String, String> params = new HashMap<>();
    params.put("A", "B");

    final OrderItem orderItem = new OrderItem();
    orderItem.setItemId("I#0001");
    orderItem.setQuantity(10);
    orderItem.setAdditionalParams(params);

    orderItems.add(orderItem);
    orderLine.setOrderItems(orderItems);
    order.setOrderLine(orderLine);

    return order;
  }
}
