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

import org.mybatis.jpetstore.domain.Cart;

public class SubmittedCheckoutState implements CheckoutState {

  @Override
  public String proceed(CheckoutContext context) {
    if (context.getOrder() == null) {
      context.getModel().addAttribute("message", "An error occurred processing your order (order was null).");

      return CheckoutContext.ERROR_VIEW;
    }

    context.getOrderService().insertOrder(context.getOrder());
    context.getSession().setAttribute("cart", new Cart());

    context.getModel().addAttribute("message", "Thank you, your order has been submitted.");

    context.getModel().addAttribute("order", context.getOrder());

    return CheckoutContext.VIEW_ORDER_VIEW;
  }

}