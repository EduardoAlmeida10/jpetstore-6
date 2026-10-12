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
package org.mybatis.jpetstore.web.checkout;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mybatis.jpetstore.domain.Order;
import org.mybatis.jpetstore.service.OrderService;
import org.springframework.ui.Model;

@ExtendWith(MockitoExtension.class)
class SubmittedCheckoutStateTest {

  @Mock
  private OrderService orderService;

  @Mock
  private HttpSession session;

  @Mock
  private Model model;

  @Test
  void shouldSubmitOrder() {
    Order order = new Order();

    CheckoutContext context = new CheckoutContext(orderService, session, model, order);

    String view = new SubmittedCheckoutState().proceed(context);

    assertThat(view).isEqualTo(CheckoutContext.VIEW_ORDER_VIEW);

    verify(orderService).insertOrder(order);
    verify(model).addAttribute("message", "Thank you, your order has been submitted.");
    verify(model).addAttribute("order", order);
  }

  @Test
  void shouldReturnErrorWhenOrderIsNull() {
    CheckoutContext context = new CheckoutContext(orderService, session, model, null);

    String view = new SubmittedCheckoutState().proceed(context);

    assertThat(view).isEqualTo(CheckoutContext.ERROR_VIEW);

    verify(model).addAttribute("message", "An error occurred processing your order (order was null).");
  }
}