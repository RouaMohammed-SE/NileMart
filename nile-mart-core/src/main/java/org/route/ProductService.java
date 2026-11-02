package org.route;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final PricingService pricingService;

    public BigDecimal getPrice(String sku, int qty) {
        Product product = findBySku(sku);
        if (!product.isActive()) {
            throw new StoreException("Product isn't available.");
        }
        return pricingService.getPrice(product, qty).setScale(2, RoundingMode.HALF_UP);
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    public Product findBySku(String sku) {
        return repository.findBySku(sku)
                .orElseThrow(() -> new StoreException("Product not found: " + sku));
    }

    public void reduceStock(String sku, int qty) {
        Product product = findBySku(sku);
        if (qty <= 0) {
            throw new StoreException("Quantity must be positive.");
        }
        if (product.getStockQuantity() < qty) {
            throw new StoreException("Not enough stock for " + product.getName()
                    + ": only " + product.getStockQuantity() + " available.");
        }
        product.setStockQuantity(product.getStockQuantity() - qty);
        repository.save(product);
    }
}
