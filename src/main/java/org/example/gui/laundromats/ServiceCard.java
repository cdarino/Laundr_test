package org.example.gui.laundromats;

import org.example.gui.utils.creators.iconCreator;
import org.example.gui.utils.creators.roundedPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Service card with a responsive icon that scales with available space.
 * - Transparent background (outer rounded border is provided by parent card).
 * - Uses a fixed-height spacer between icon and label so the gap is reliable.
 * - Icon size recomputed on resize so it grows/shrinks with window.
 */
public class ServiceCard extends roundedPanel {

    private final String iconFile;
    private final JLabel iconLabel;
    private final JLabel lbl;
    private final Component spacer;

    // Tunables: clamp icon size between these bounds
    private static final int MIN_ICON_PX = 48;
    private static final int MAX_ICON_PX = 88;   // slightly conservative
    // Gap between image and text (adjust to taste)
    private static final int ICON_TEXT_GAP = 10; // increased gap

    public ServiceCard(String text, String iconFile) {
        super(20);
        this.iconFile = iconFile;

        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(150, 110));

        iconLabel = new JLabel();
        iconLabel.setAlignmentX(0.5f);

        spacer = Box.createVerticalStrut(ICON_TEXT_GAP);

        lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setAlignmentX(0.5f);

        add(Box.createVerticalGlue());
        add(iconLabel);
        add(spacer); // explicit, reliable gap between image and text
        add(lbl);
        add(Box.createVerticalGlue());

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                updateIconSize();
            }
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(this::updateIconSize);
    }

    private void updateIconSize() {
        int w = Math.max(0, getWidth());
        int h = Math.max(0, getHeight());
        if (w == 0 || h == 0) return;

        // Leave room for the spacer and the label
        int labelH = lbl.getPreferredSize().height + ICON_TEXT_GAP;
        int availHForIcon = Math.max(0, h - labelH - 8);

        // Slightly conservative scaling
        int sizeByHeight = (int) Math.round(availHForIcon * 0.65);
        int sizeByWidth  = (int) Math.round(w * 0.50);

        int target = Math.min(sizeByHeight, sizeByWidth);
        target = Math.max(MIN_ICON_PX, Math.min(MAX_ICON_PX, target));

        Icon current = iconLabel.getIcon();
        int currentW = (current instanceof ImageIcon) ? ((ImageIcon) current).getIconWidth() : -1;
        if (target != currentW) {
            iconLabel.setIcon(iconCreator.getIcon(iconFile, target, target));
            revalidate();
            repaint();
        }
    }
}