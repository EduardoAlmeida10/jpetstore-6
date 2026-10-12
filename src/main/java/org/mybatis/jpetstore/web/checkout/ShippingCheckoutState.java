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

public class ShippingCheckoutState implements CheckoutState {

  @Override
  public String proceed(CheckoutContext context) {
    context.getSession().setAttribute("order", context.getOrder());
    context.getModel().addAttribute("order", context.getOrder());

    return CheckoutContext.SHIPPING_VIEW;
  }

}