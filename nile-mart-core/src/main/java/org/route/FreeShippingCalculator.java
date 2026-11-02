package org.route;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("freeShipping")
public class FreeShippingCalculator implements ShippingCalculator {

    @Override
    public BigDecimal calculate(Governorate g) {
        return BigDecimal.ZERO.setScale(2);
    }
}
