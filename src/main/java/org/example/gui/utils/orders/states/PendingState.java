package org.example.gui.utils.orders.states;

import java.awt.Color;

// state for 'pending'
public class PendingState implements OrderState {
    @Override
    public String getText() {
        return "PENDING";
    }

    @Override
    public Color getColor() {
        return new Color(0xFBBF24);
    }
}