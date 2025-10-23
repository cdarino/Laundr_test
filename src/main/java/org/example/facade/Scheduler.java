package org.example.facade;

import java.time.LocalDateTime;

/**
 * Small scheduling helper — returns a pickup time.
 * Replace with real scheduling logic later.
 */
public class Scheduler {
    /**
     * Returns earliest available pickup time. Simple simulation:
     * now + 2 hours.
     */
    public LocalDateTime schedulePickup() {
        return LocalDateTime.now().plusHours(2);
    }
}