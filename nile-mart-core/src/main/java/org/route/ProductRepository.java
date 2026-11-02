package org.route;

import java.util.Optional;
import java.util.List;

public interface ProductRepository {
    Optional<Product> findBySku(String sku);
    List<Product> findAll();
    Product save(Product product);

}
