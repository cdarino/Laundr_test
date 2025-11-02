package org.example.facade;

import org.example.order.LaundryOrder;

/**
 * Simple notification subsystem. For now, it logs notifications.
 * Later: hook into Observer pattern or push to UI.
 */
public class NotificationSubsystem {
    public void notifyCustomer(LaundryOrder order, String message) {
        // Replace with SMS/email or UI event. For now: print/log.
        System.out.printf("[Notification] Order %s -> %s%n", order.getId(), message);
    }
}