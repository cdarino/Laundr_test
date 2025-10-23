package org.example.facade;

import org.example.order.LaundryOrder;

/**
 * High-level facade that hides scheduling, pricing, payment, and notification.
 *
 * Usage:
 *   OrderProcessingFacade facade = new OrderProcessingFacade();
 *   boolean ok = facade.placeOrder(order, "card-****-1234");
 */
public class OrderProcessingFacade {
    private final Scheduler scheduler = new Scheduler();
    private final PricingService pricing = new PricingService();
    private final PaymentProcessor payment = new PaymentProcessor();
    private final NotificationSubsystem notifier = new NotificationSubsystem();

    /**
     * Places an order: schedules pickup, calculates price, charges payment,
     * and notifies customer. Returns true if order placement succeeded.
     *
     * Side-effects: logs/prints notifications. Keep low coupling: do not mutate order content.
     */
    public boolean placeOrder(LaundryOrder order, String paymentInfo) {
        if (order == null) throw new IllegalArgumentException("order is null");

        // 1) schedule pickup
        var pickupAt = scheduler.schedulePickup();
        notifier.notifyCustomer(order, "Pickup scheduled at: " + pickupAt);

        // 2) compute price
        double total = pricing.calculateTotal(order);
        notifier.notifyCustomer(order, String.format("Total calculated: %.2f", total));

        // 3) attempt payment
        boolean paid = payment.charge(paymentInfo, total);
        if (!paid) {
            notifier.notifyCustomer(order, "Payment failed.");
            return false;
        }
        notifier.notifyCustomer(order, "Payment successful. Order placed.");

        // 4) return success — state transitions / deliveries handled by next patterns (State / Observer)
        return true;
    }
}