package org.example.gui.utils.orders.states;

import java.awt.Color;

// state for 'ready_for_delivery'
public class ReadyForDeliveryState implements OrderState {
    @Override
    public String getText() {
        return "READY";
    }

    @Override
    public Color getColor() {
        return new Color(0xA855F7);
    }
}