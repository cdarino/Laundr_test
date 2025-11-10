package org.example.gui.utils.orders.states;

import java.awt.Color;

/**
 * interface for the state pattern.
 * each state knows its own text and color.
 */
public interface OrderState {
    String getText();
    Color getColor();
}