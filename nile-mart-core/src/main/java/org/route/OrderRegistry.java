package org.route;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class OrderRegistry {

    private final List<Order> orders = new ArrayList<>();
    private int sequence = 0;

    public String nextOrderNumber() {
        sequence++;
        return String.format("NM-%s-%05d",
                LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE), sequence);
    }

    public void save(Order order) {
        orders.add(order);
    }

    public List<Order> findAll() {
        return List.copyOf(orders);
    }

    @PreDestroy
    void endOfDay() {
        BigDecimal revenue = orders.stream()
                .map(Order::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println(String.format(Locale.US, "End of day: %d orders, revenue %,.2f EGP",
                orders.size(), revenue));
    }
}
