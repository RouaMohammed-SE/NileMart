package org.route.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;

@Value
@AllArgsConstructor
public class ProductRow {
    String sku;
    String name;
    BigDecimal price;
    Integer inStock;
    String categoryName;
    String brandName;
}
