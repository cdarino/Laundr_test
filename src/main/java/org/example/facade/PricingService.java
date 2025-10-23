package org.example.facade;

import org.example.order.LaundryOrder;

/** Pricing calculation. Keep it simple so you can swap in real logic later. */
public class PricingService {
    /**
     * Calculate total price from order subtotal, add tax, and apply simple discount rule.
     */
    public double calculateTotal(LaundryOrder order) {
        double subtotal = order.subtotal();
        double tax = subtotal * 0.12; // 12% VAT-like tax
        double discount = subtotal > 500 ? 50.0 : 0.0; // simple threshold discount
        return subtotal + tax - discount;
    }
}