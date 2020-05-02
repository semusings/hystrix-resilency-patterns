package io.github.bhuwanupadhyay.dynamodb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderRepositoryTest {

  @Autowired private OrderRepository repository;

  @Test
  void canSave() {
    final Order order = order();
    order.setOrderId(UUID.randomUUID().toString());
    try {
      repository.save(order);
    } catch (Exception e) {
      fail("Not saved", e);
    }
  }

  private Order order() {
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
