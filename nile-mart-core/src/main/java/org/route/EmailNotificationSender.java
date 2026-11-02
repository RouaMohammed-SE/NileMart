package org.route;

import org.springframework.stereotype.Component;

@Component
public class EmailNotificationSender implements NotificationSender {

    @Override
    public void send(Customer customer, String message) {
        if (customer.email() == null || customer.email().isBlank()) {
            return;
        }
        System.out.println("[EMAIL] to " + customer.email() + ": " + message);
    }
}
