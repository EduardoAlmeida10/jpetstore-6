/*
 *    Copyright 2010-2026 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.jpetstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mybatis.jpetstore.domain.Order;
import org.mybatis.jpetstore.service.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock
  private OrderRepository orderRepository;

  @InjectMocks
  private OrderService orderService;

  @Test
  void shouldReturnOrderWhenGivenOrderId() {
    int orderId = 1;
    Order expectedOrder = new Order();

    when(orderRepository.findById(orderId)).thenReturn(expectedOrder);

    Order order = orderService.getOrder(orderId);

    assertThat(order).isSameAs(expectedOrder);
    verify(orderRepository).findById(orderId);
  }

  @Test
  void shouldReturnOrderList() {
    String username = "foo";
    List<Order> expectedOrders = new ArrayList<>();

    when(orderRepository.findByUsername(username)).thenReturn(expectedOrders);

    List<Order> orders = orderService.getOrdersByUsername(username);

    assertThat(orders).isSameAs(expectedOrders);
    verify(orderRepository).findByUsername(username);
  }

  @Test
  void shouldReturnNextId() {
    when(orderRepository.getNextId("order")).thenReturn(100);

    int nextId = orderService.getNextId("order");

    assertThat(nextId).isEqualTo(100);
    verify(orderRepository).getNextId("order");
  }

  @Test
  void shouldCallRepositoryToInsert() {
    Order order = new Order();

    orderService.insertOrder(order);

    verify(orderRepository).save(order);
  }
}