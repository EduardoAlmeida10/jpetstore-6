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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mock.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mybatis.jpetstore.domain.Category;
import org.mybatis.jpetstore.domain.Item;
import org.mybatis.jpetstore.domain.Product;
import org.mybatis.jpetstore.mapper.CategoryMapper;
import org.mybatis.jpetstore.mapper.ItemMapper;
import org.mybatis.jpetstore.mapper.ProductMapper;
import org.mybatis.jpetstore.service.search.ProductSearchStrategy;

/**
 * The Class CatalogServiceTest.
 */
@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

  /** The product mapper. */
  @Mock(strictness = Strictness.LENIENT)
  private ProductMapper productMapper;
  /** The category mapper. */
  @Mock
  private CategoryMapper categoryMapper;
  /** The item mapper. */
  @Mock
  private ItemMapper itemMapper;

  /** The catalog service. */
  @Mock
  private ProductSearchStrategy keywordSearchStrategy;

  @Mock
  private ProductSearchStrategy categorySearchStrategy;

  private CatalogService catalogService;

  @BeforeEach
  void setUp() {
    catalogService = new CatalogService(categoryMapper, itemMapper, productMapper, keywordSearchStrategy,
        categorySearchStrategy);
  }

  /**
   * Should call the search mapper twice.
   */
  @Test
  void shouldDelegateKeywordSearchToStrategy() {
    String keywords = "fish dog";
    List<Product> expectedProducts = new ArrayList<>();

    when(keywordSearchStrategy.search(keywords)).thenReturn(expectedProducts);

    List<Product> products = catalogService.searchProductList(keywords);

    assertThat(products).isSameAs(expectedProducts);
    verify(keywordSearchStrategy).search(keywords);
  }

  /**
   * Should return category list.
   */
  @Test
  void shouldReturnCategoryList() {
    // given
    List<Category> expectedCategories = new ArrayList<>();

    // when
    when(categoryMapper.getCategoryList()).thenReturn(expectedCategories);
    List<Category> categories = catalogService.getCategoryList();

    // then
    assertThat(categories).isSameAs(expectedCategories);
  }

  /**
   * Should return category.
   */
  @Test
  void shouldReturnCategory() {

    // given
    String categoryId = "C01";
    Category expectedCategory = new Category();

    // when
    when(categoryMapper.getCategory(categoryId)).thenReturn(expectedCategory);
    Category category = catalogService.getCategory(categoryId);

    // then
    assertThat(category).isSameAs(expectedCategory);

  }

  /**
   * Should return product.
   */
  @Test
  void shouldReturnProduct() {

    // given
    String productId = "P01";
    Product expectedProduct = new Product();

    // when
    when(productMapper.getProduct(productId)).thenReturn(expectedProduct);
    Product product = catalogService.getProduct(productId);

    // then
    assertThat(product).isSameAs(expectedProduct);

  }

  /**
   * Should return product list.
   */
  @Test
  void shouldReturnProductList() {
    String categoryId = "DOGS";
    List<Product> expectedProducts = new ArrayList<>();

    when(categorySearchStrategy.search(categoryId)).thenReturn(expectedProducts);

    List<Product> products = catalogService.getProductListByCategory(categoryId);

    assertThat(products).isSameAs(expectedProducts);
    verify(categorySearchStrategy).search(categoryId);
  }

  /**
   * Should return item list.
   */
  @Test
  void shouldReturnItemList() {
    // given
    String productId = "P01";
    List<Item> expectedItems = new ArrayList<>();

    // when
    when(itemMapper.getItemListByProduct(productId)).thenReturn(expectedItems);
    List<Item> items = catalogService.getItemListByProduct(productId);

    // then
    assertThat(items).isSameAs(expectedItems);

  }

  /**
   * Should return item.
   */
  @Test
  void shouldReturnItem() {

    // given
    String itemCode = "I01";
    Item expectedItem = new Item();

    // when
    when(itemMapper.getItem(itemCode)).thenReturn(expectedItem);
    Item item = catalogService.getItem(itemCode);

    // then
    assertThat(item).isSameAs(expectedItem);

  }

  /**
   * Should return true when exist stock.
   */
  @Test
  void shouldReturnTrueWhenExistStock() {

    // given
    String itemCode = "I01";

    // when
    when(itemMapper.getInventoryQuantity(itemCode)).thenReturn(1);
    boolean result = catalogService.isItemInStock(itemCode);

    // then
    assertThat(result).isTrue();

  }

  /**
   * Should return false when not exist stock.
   */
  @Test
  void shouldReturnFalseWhenNotExistStock() {

    // given
    String itemCode = "I01";

    // when
    when(itemMapper.getInventoryQuantity(itemCode)).thenReturn(0);
    boolean result = catalogService.isItemInStock(itemCode);

    // then
    assertThat(result).isFalse();

  }
}
