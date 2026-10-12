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
package org.mybatis.jpetstore.domain;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;

public class OrderBuilder {
  private final Order order = new Order();

  public OrderBuilder fromAccount(Account account) {
    order.setUsername(account.getUsername());

    order.setShipToFirstName(account.getFirstName());
    order.setShipToLastName(account.getLastName());
    order.setShipAddress1(account.getAddress1());
    order.setShipAddress2(account.getAddress2());
    order.setShipCity(account.getCity());
    order.setShipState(account.getState());
    order.setShipZip(account.getZip());
    order.setShipCountry(account.getCountry());

    order.setBillToFirstName(account.getFirstName());
    order.setBillToLastName(account.getLastName());
    order.setBillAddress1(account.getAddress1());
    order.setBillAddress2(account.getAddress2());
    order.setBillCity(account.getCity());
    order.setBillState(account.getState());
    order.setBillZip(account.getZip());
    order.setBillCountry(account.getCountry());

    return this;
  }

  public OrderBuilder fromCart(Cart cart) {
    order.setTotalPrice(cart.getSubTotal());

    Iterator<CartItem> items = cart.getAllCartItems();

    while (items.hasNext()) {
      order.addLineItem(items.next());
    }

    return this;
  }

  public OrderBuilder withDefaults() {
    order.setOrderDate(LocalDateTime.now(ZoneId.systemDefault()));
    order.setCreditCard("999 9999 9999 9999");
    order.setExpiryDate("12/03");
    order.setCardType("Visa");
    order.setCourier("UPS");
    order.setLocale("CA");
    order.setStatus("P");

    return this;
  }

  public Order build() {
    return order;
  }
}
