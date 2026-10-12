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
package org.mybatis.jpetstore.service.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mybatis.jpetstore.domain.Item;
import org.mybatis.jpetstore.domain.LineItem;
import org.mybatis.jpetstore.domain.Order;
import org.mybatis.jpetstore.domain.Sequence;
import org.mybatis.jpetstore.mapper.ItemMapper;
import org.mybatis.jpetstore.mapper.LineItemMapper;
import org.mybatis.jpetstore.mapper.OrderMapper;
import org.mybatis.jpetstore.mapper.SequenceMapper;

@ExtendWith(MockitoExtension.class)
class MyBatisOrderRepositoryAdapterTest {

  @Mock
  private ItemMapper itemMapper;

  @Mock
  private OrderMapper orderMapper;

  @Mock
  private LineItemMapper lineItemMapper;

  @Mock
  private SequenceMapper sequenceMapper;

  @InjectMocks
  private MyBatisOrderRepositoryAdapter adapter;

  @Test
  void shouldReturnOrderWithoutLineItems() {
    int orderId = 1;
    Order order = new Order();
    List<LineItem> lineItems = new ArrayList<>();

    when(orderMapper.getOrder(orderId)).thenReturn(order);
    when(lineItemMapper.getLineItemsByOrderId(orderId)).thenReturn(lineItems);

    Order result = adapter.findById(orderId);

    assertThat(result).isSameAs(order);
    assertThat(result.getLineItems()).isEmpty();
  }

  @Test
  void shouldReturnOrderWithLineItems() {
    int orderId = 1;
    Order order = new Order();

    LineItem lineItem = new LineItem();
    lineItem.setItemId("abc");

    List<LineItem> lineItems = new ArrayList<>();
    lineItems.add(lineItem);

    when(orderMapper.getOrder(orderId)).thenReturn(order);
    when(lineItemMapper.getLineItemsByOrderId(orderId)).thenReturn(lineItems);
    when(itemMapper.getItem("abc")).thenReturn(new Item());
    when(itemMapper.getInventoryQuantity("abc")).thenReturn(5);

    Order result = adapter.findById(orderId);

    assertThat(result).isSameAs(order);
    assertThat(result.getLineItems()).hasSize(1);
    assertThat(result.getLineItems().get(0).getItem().getQuantity()).isEqualTo(5);
  }

  @Test
  void shouldReturnOrdersByUsername() {
    String username = "foo";
    List<Order> expectedOrders = new ArrayList<>();

    when(orderMapper.getOrdersByUsername(username)).thenReturn(expectedOrders);

    List<Order> orders = adapter.findByUsername(username);

    assertThat(orders).isSameAs(expectedOrders);
  }

  @Test
  void shouldReturnNextId() {
    Sequence sequence = new Sequence("order", 100);

    when(sequenceMapper.getSequence(any())).thenReturn(sequence);

    int nextId = adapter.getNextId("order");

    assertThat(nextId).isEqualTo(100);

    verify(sequenceMapper).getSequence(argThat(value -> value.getName().equals("order") && value.getNextId() == -1));

    verify(sequenceMapper)
        .updateSequence(argThat(value -> value.getName().equals("order") && value.getNextId() == 101));
  }

  @Test
  void shouldThrowExceptionWhenSequenceNotFound() {
    when(sequenceMapper.getSequence(any())).thenReturn(null);

    assertThatThrownBy(() -> adapter.getNextId("order")).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Error: A null sequence was returned from the database (could not get next order sequence).");
  }

  @Test
  void shouldSaveOrder() {
    Order order = new Order();

    LineItem lineItem = new LineItem();
    lineItem.setItemId("I01");
    lineItem.setQuantity(4);

    order.addLineItem(lineItem);

    Sequence sequence = new Sequence("ordernum", 100);

    Map<String, Object> expectedItemParam = new HashMap<>(2);
    expectedItemParam.put("itemId", "I01");
    expectedItemParam.put("increment", 4);

    when(sequenceMapper.getSequence(any())).thenReturn(sequence);

    adapter.save(order);

    verify(orderMapper).insertOrder(argThat(value -> value == order && value.getOrderId() == 100));

    verify(orderMapper).insertOrderStatus(eq(order));

    verify(lineItemMapper).insertLineItem(argThat(value -> value == lineItem && value.getOrderId() == 100));

    verify(itemMapper).updateInventoryQuantity(eq(expectedItemParam));
  }
}