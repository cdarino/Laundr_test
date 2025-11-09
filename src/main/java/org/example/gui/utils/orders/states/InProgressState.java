package org.example.gui.utils.orders.states;

import java.awt.Color;

// state for 'in_progress'
public class InProgressState implements OrderState {
    @Override
    public String getText() {
        return "IN PROGRESS";
    }

    @Override
    public Color getColor() {
        return new Color(0x3B82F6);
    }
}