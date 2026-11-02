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
}
