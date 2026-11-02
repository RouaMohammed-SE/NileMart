package org.route;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Scope("prototype")
public class ShoppingCart {

    private final ProductService productService;
    private final int maxQuantityPerProduct;

    @Getter @Setter private int cartNumber;
    @Getter @Setter private Customer customer;
    @Getter private final Map<String, Integer> lines = new LinkedHashMap<>();

    public ShoppingCart(ProductService productService,
                        @Value("${store.max-quantity-per-product}") int maxQuantityPerProduct) {
        this.productService = productService;
        this.maxQuantityPerProduct = maxQuantityPerProduct;
    }

    public void addItem(String sku, int qty) {
        if (qty <= 0) {
            throw new StoreException("Quantity must be positive.");
        }
        Product product = productService.findBySku(sku);
        if (product.getStockQuantity() <= 0) {                       // CART-01
            throw new StoreException(product.getName() + " is out of stock.");
        }
        int newQty = lines.getOrDefault(sku, 0) + qty;               // CART-02
        if (newQty > maxQuantityPerProduct) {                        // CART-03
            throw new StoreException("Maximum " + maxQuantityPerProduct
                    + " per product (cart already has " + lines.getOrDefault(sku, 0) + ").");
        }
        if (newQty > product.getStockQuantity()) {
            throw new StoreException("Only " + product.getStockQuantity()
                    + " available for " + product.getName() + ".");
        }
        lines.put(sku, newQty);
    }

    public BigDecimal subtotal() {
        return lines.entrySet().stream()
                .map(e -> productService.getPrice(e.getKey(), e.getValue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public void clear() {
        lines.clear();
    }
}
