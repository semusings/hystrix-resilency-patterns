package io.github.bhuwanupadhyay.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExternalOrderServiceTest {

  @Autowired private ExternalOrderService externalOrderService;

  @Test
  void canPlaceOrderSuccessfully() {
    Order order = order();
    order.setOrderId(UUID.randomUUID().toString());
    externalOrderService.createOrder(order);
  }

  @Test
  void whenNotPlaceOrderThenWriteToDLQ() {
    Order order = order();
    externalOrderService.createOrder(order);
  }

  private Order order() {
    final Order order = new Order();
    final OrderLine orderLine = new OrderLine();

    final List<OrderItem> orderItems = new ArrayList<>();

    final HashMap<String, String> params = new HashMap<>();
    params.put("A", "B");

    final OrderItem orderItem = new OrderItem();
    orderItem.setItemId("I#0001");
    orderItem.setQuantity(10);
    orderItem.setItemStatus(OrderItemStatus.AVAILABLE);
    orderItem.setAdditionalParams(params);

    orderItems.add(orderItem);
    orderLine.setOrderItems(orderItems);
    order.setOrderLine(orderLine);

    order.setOrderStatus(OrderStatus.DELIVERED);
    return order;
  }
}
