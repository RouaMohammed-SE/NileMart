package org.route;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final ProductService productService;
    private final ShippingQuoteService shippingQuoteService;
    private final TaxCalculator taxCalculator;
    private final OrderRegistry orderRegistry;
    private final List<NotificationSender> senders;

    public Order placeOrder(ShoppingCart cart, Governorate governorate) {
        // 1. reject an empty cart
        if (cart.isEmpty()) {
            throw new StoreException("Cart is empty.");
        }
        // 2. re-check the stock of every line before changing anything
        for (Map.Entry<String, Integer> line : cart.getLines().entrySet()) {
            Product p = productService.findBySku(line.getKey());
            if (p.getStockQuantity() < line.getValue()) {
                throw new StoreException("Not enough stock for " + p.getName()
                        + ": only " + p.getStockQuantity() + " available.");
            }
        }
        // 3. totals
        BigDecimal subtotal = cart.subtotal();
        BigDecimal vat = taxCalculator.calculate(subtotal);
        BigDecimal shipping = shippingQuoteService.quote(governorate, subtotal);
        BigDecimal total = subtotal.add(vat).add(shipping);

        // 4. reduce stock for all lines
        cart.getLines().forEach(productService::reduceStock);

        // 5. save the order
        Order order = new Order(orderRegistry.nextOrderNumber(), cart.getCustomer(), governorate,
                Map.copyOf(cart.getLines()), subtotal, vat, shipping, total);
        orderRegistry.save(order);

        // 6. notify through every sender
        String message = String.format(Locale.US, "Order %s confirmed. Total %,.2f EGP",
                order.orderNumber(), total);
        senders.forEach(s -> s.send(order.customer(), message));

        cart.clear();
        return order;
    }
}
