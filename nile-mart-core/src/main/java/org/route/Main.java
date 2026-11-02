package org.route;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            ShippingQuoteService shipping = context.getBean(ShippingQuoteService.class);

            BigDecimal aswan = shipping.quote(Governorate.ASWAN, new BigDecimal("16000.00"));
            BigDecimal southSinai = shipping.quote(Governorate.SOUTH_SINAI, new BigDecimal("16000.00"));

            System.out.println("ASWAN: " + aswan);
            System.out.println("SOUTH_SINAI: " + southSinai);

            if (aswan.compareTo(BigDecimal.ZERO) != 0
                    || southSinai.compareTo(new BigDecimal("120.00")) != 0) {
                throw new AssertionError("Shipping quote check failed.");
            }
            System.out.println("Shipping checks passed.");

            Customer customer = new Customer("Mona", "01012345678", "mona@example.com");
            Customer customerWithoutEmail = new Customer("Ali", "01098765432", null);
            context.getBeansOfType(NotificationSender.class)
                    .values()
                    .forEach(sender -> sender.send(customer, "Test order confirmation"));
            context.getBean(EmailNotificationSender.class)
                    .send(customerWithoutEmail, "This should not print");
            System.out.println("Notification checks passed (no email line should follow for Ali).");

            ShoppingCart cart = context.getBean(ShoppingCart.class);
            expectCartFailure(cart, "BTY-SUN-50", 1, "out of stock");
            expectCartFailure(cart, "HOM-PAN-28", 4, "Only 3 available");
            if (!cart.isEmpty()) {
                throw new AssertionError("Failed additions should not change the cart.");
            }
            System.out.println("Cart stock checks passed.");

            ProductService products = context.getBean(ProductService.class);
            CheckoutService checkout = context.getBean(CheckoutService.class);
            OrderRegistry orders = context.getBean(OrderRegistry.class);

            ShoppingCart emptyCart = context.getBean(ShoppingCart.class);
            emptyCart.setCustomer(new Customer("Empty Cart", "01000000000", null));
            expectCheckoutFailure(checkout, emptyCart, Governorate.GIZA, "Cart is empty.");
            if (!orders.findAll().isEmpty()) {
                throw new AssertionError("An empty cart must not create an order.");
            }

            ShoppingCart checkoutCart = context.getBean(ShoppingCart.class);
            checkoutCart.setCustomer(customer);
            checkoutCart.addItem("ACC-CHG-20W", 2);
            checkoutCart.addItem("FSH-GAL-001", 1);

            int chargerStockBefore = products.findBySku("ACC-CHG-20W").getStockQuantity();
            int galabeyaStockBefore = products.findBySku("FSH-GAL-001").getStockQuantity();
            Order order = checkout.placeOrder(checkoutCart, Governorate.GIZA);

            assertMoney("subtotal", order.subtotal(), "1980.00");
            assertMoney("VAT", order.vat(), "277.20");
            assertMoney("shipping", order.shippingFee(), "50.00");
            assertMoney("total", order.total(), "2307.20");
            if (!order.orderNumber().matches("NM-\\d{8}-00001")) {
                throw new AssertionError("Unexpected order number: " + order.orderNumber());
            }
            if (products.findBySku("ACC-CHG-20W").getStockQuantity() != chargerStockBefore - 2
                    || products.findBySku("FSH-GAL-001").getStockQuantity() != galabeyaStockBefore - 1) {
                throw new AssertionError("Successful checkout did not reduce stock correctly.");
            }
            if (!checkoutCart.isEmpty() || orders.findAll().size() != 1) {
                throw new AssertionError("Successful checkout should save one order and clear the cart.");
            }

            ShoppingCart staleCart = context.getBean(ShoppingCart.class);
            staleCart.setCustomer(customer);
            staleCart.addItem("HOM-PAN-28", 2);
            products.reduceStock("HOM-PAN-28", 2);
            int panStockBeforeFailedCheckout = products.findBySku("HOM-PAN-28").getStockQuantity();
            expectCheckoutFailure(checkout, staleCart, Governorate.GIZA, "Not enough stock");
            if (products.findBySku("HOM-PAN-28").getStockQuantity() != panStockBeforeFailedCheckout
                    || staleCart.getLines().get("HOM-PAN-28") != 2
                    || orders.findAll().size() != 1) {
                throw new AssertionError("Failed checkout changed stock, cart, or saved orders.");
            }
            System.out.println("Checkout checks passed.");
        }
    }

    private static void expectCartFailure(ShoppingCart cart, String sku, int qty, String expectedMessage) {
        try {
            cart.addItem(sku, qty);
            throw new AssertionError("Expected adding " + sku + " to fail.");
        } catch (StoreException exception) {
            if (!exception.getMessage().contains(expectedMessage)) {
                throw new AssertionError("Unexpected cart error: " + exception.getMessage(), exception);
            }
            System.out.println("Expected cart rejection: " + exception.getMessage());
        }
    }

    private static void expectCheckoutFailure(CheckoutService checkout, ShoppingCart cart,
                                              Governorate governorate, String expectedMessage) {
        try {
            checkout.placeOrder(cart, governorate);
            throw new AssertionError("Expected checkout to fail.");
        } catch (StoreException exception) {
            if (!exception.getMessage().contains(expectedMessage)) {
                throw new AssertionError("Unexpected checkout error: " + exception.getMessage(), exception);
            }
            System.out.println("Expected checkout rejection: " + exception.getMessage());
        }
    }

    private static void assertMoney(String label, BigDecimal actual, String expected) {
        if (actual.compareTo(new BigDecimal(expected)) != 0) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
        System.out.println(label + " verified: " + actual);
    }
}
