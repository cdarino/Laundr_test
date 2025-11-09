package org.example.gui.utils.orders.states;

import java.awt.Color;

// state for 'out_for_delivery'
public class OutForDeliveryState implements OrderState {
    @Override
    public String getText() {
        return "OUT FOR DELIVERY";
    }

    @Override
    public Color getColor() {
        return new Color(0x14B8A6);
    }
}