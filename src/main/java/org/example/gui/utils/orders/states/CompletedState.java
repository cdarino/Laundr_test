package org.example.gui.utils.orders.states;

import java.awt.Color;

// state for 'completed'
public class CompletedState implements OrderState {
    @Override
    public String getText() {
        return "COMPLETED";
    }

    @Override
    public Color getColor() {
        return new Color(0x22C55E);
    }
}