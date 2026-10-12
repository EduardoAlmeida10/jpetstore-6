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
package org.mybatis.jpetstore.service.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mybatis.jpetstore.domain.Product;
import org.mybatis.jpetstore.mapper.ProductMapper;

@ExtendWith(MockitoExtension.class)
class KeywordProductSearchStrategyTest {

  @Mock
  private ProductMapper productMapper;

  @InjectMocks
  private KeywordProductSearchStrategy strategy;

  @Test
  void shouldSearchEachKeyword() {
    Product firstProduct = new Product();
    Product secondProduct = new Product();

    when(productMapper.searchProductList("%fish%")).thenReturn(List.of(firstProduct));
    when(productMapper.searchProductList("%dog%")).thenReturn(List.of(secondProduct));

    List<Product> products = strategy.search("Fish  DOG");

    assertThat(products).containsExactly(firstProduct, secondProduct);

    verify(productMapper).searchProductList("%fish%");
    verify(productMapper).searchProductList("%dog%");
  }

}