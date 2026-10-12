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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.jpetstore.domain.Item;
import org.mybatis.jpetstore.domain.Order;
import org.mybatis.jpetstore.domain.Sequence;
import org.mybatis.jpetstore.mapper.ItemMapper;
import org.mybatis.jpetstore.mapper.LineItemMapper;
import org.mybatis.jpetstore.mapper.OrderMapper;
import org.mybatis.jpetstore.mapper.SequenceMapper;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisOrderRepositoryAdapter implements OrderRepository {
  private final ItemMapper itemMapper;
  private final OrderMapper orderMapper;
  private final SequenceMapper sequenceMapper;
  private final LineItemMapper lineItemMapper;

  public MyBatisOrderRepositoryAdapter(ItemMapper itemMapper, OrderMapper orderMapper, SequenceMapper sequenceMapper,
      LineItemMapper lineItemMapper) {
    this.itemMapper = itemMapper;
    this.orderMapper = orderMapper;
    this.sequenceMapper = sequenceMapper;
    this.lineItemMapper = lineItemMapper;
  }

  @Override
  public void save(Order order) {
    order.setOrderId(getNextId("ordernum"));

    order.getLineItems().forEach(lineItem -> {
      String itemId = lineItem.getItemId();
      Integer increment = lineItem.getQuantity();

      Map<String, Object> param = new HashMap<>(2);
      param.put("itemId", itemId);
      param.put("increment", increment);

      itemMapper.updateInventoryQuantity(param);
    });

    orderMapper.insertOrder(order);
    orderMapper.insertOrderStatus(order);

    order.getLineItems().forEach(lineItem -> {
      lineItem.setOrderId(order.getOrderId());
      lineItemMapper.insertLineItem(lineItem);
    });
  }

  @Override
  public Order findById(int orderId) {
    Order order = orderMapper.getOrder(orderId);

    order.setLineItems(lineItemMapper.getLineItemsByOrderId(orderId));

    order.getLineItems().forEach(lineItem -> {
      Item item = itemMapper.getItem(lineItem.getItemId());
      item.setQuantity(itemMapper.getInventoryQuantity(lineItem.getItemId()));
      lineItem.setItem(item);
    });

    return order;
  }

  @Override
  public List<Order> findByUsername(String username) {
    return orderMapper.getOrdersByUsername(username);
  }

  @Override
  public int getNextId(String name) {
    Sequence sequence = sequenceMapper.getSequence(new Sequence(name, -1));

    if (sequence == null) {
      throw new IllegalArgumentException(
          "Error: A null sequence was returned from the database (could not get next " + name + " sequence).");
    }

    Sequence parameterObject = new Sequence(name, sequence.getNextId() + 1);
    sequenceMapper.updateSequence(parameterObject);

    return sequence.getNextId();
  }

}
