package org.example.facade;

/**
 * Simulated payment processor. Replace with real gateway integration later.
 */
public class PaymentProcessor {
    /**
     * Simulates charging the given payment token / info.
     * Returns true on success.
     */
    public boolean charge(String paymentInfo, double amount) {
        // In real life: call payment SDK with token + amount.
        System.out.printf("[Payment] Charging %s : %.2f%n", paymentInfo == null ? "<no-payment-info>" : paymentInfo, amount);
        return true; // simulate success for now
    }
}