package org.example.gui.utils.orders.states;

import java.awt.Color;

// state for 'accepted'
public class AcceptedState implements OrderState {
    @Override
    public String getText() {
        return "ACCEPTED";
    }

    @Override
    public Color getColor() {
        return new Color(0x60A5FA);
    }
}