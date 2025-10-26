package org.example.gui.laundromats;

import org.example.gui.utils.creators.iconCreator;
import org.example.gui.utils.creators.roundedPanel;

import javax.swing.*;
import java.awt.*;

/**
 * One service card with icon + label.
 */
public class ServiceCard extends roundedPanel {
    public ServiceCard(String text, String iconFile) {
        super(20);
        setLayout(new BorderLayout());
        setBackground(UIManager.getColor("Sidebarbtn.background"));
        setPreferredSize(new Dimension(120, 100));

        JLabel icon = new JLabel(iconCreator.getIcon(iconFile, 48, 48), SwingConstants.CENTER);
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        add(icon, BorderLayout.CENTER);

        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        add(lbl, BorderLayout.SOUTH);
    }
}
