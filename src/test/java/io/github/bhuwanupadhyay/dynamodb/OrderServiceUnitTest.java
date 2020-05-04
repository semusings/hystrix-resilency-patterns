package io.github.bhuwanupadhyay.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.amazonaws.services.sqs.AmazonSQS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {

  private OrderService orderService;

  @Mock private AmazonDynamoDB dynamoDB;
  @Mock private AmazonSQS amazonSQS;

  @BeforeEach
  void setUp() {
    this.orderService = new OrderService(dynamoDB, amazonSQS, new AppAws());
  }

  @Test
  void canPlaceOrderSuccessfully() {
    when(dynamoDB.updateItem(any(UpdateItemRequest.class))).thenReturn(new UpdateItemResult());
    Order order = newOrder();
    order.setOrderId(UUID.randomUUID().toString());
    orderService.createOrder(order);
    verify(dynamoDB).updateItem(any(UpdateItemRequest.class));
  }

  @Test
  void whenNotPlaceOrderThenWriteToDLQ() {
    when(dynamoDB.updateItem(any(UpdateItemRequest.class))).thenReturn(new UpdateItemResult());
    Order order = newOrder();
    orderService.createOrder(order);
  }

  private Order newOrder() {
    final Order order = new Order();
    final OrderLine orderLine = new OrderLine();

    final List<OrderItem> orderItems = new ArrayList<>();

    final OrderItem orderItem = new OrderItem();
    orderItem.setItemId("I#0001");
    orderItem.setQuantity(10);

    orderItems.add(orderItem);
    orderLine.setOrderItems(orderItems);
    order.setOrderLine(orderLine);

    return order;
  }
}
