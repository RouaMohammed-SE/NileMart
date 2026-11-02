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
        }
    }
}
