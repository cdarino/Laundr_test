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
 * - Label font set to Fredoka Bold, slightly larger than default (but smaller than the pickup button).
 */
public class ServiceCard extends roundedPanel {

    private final String iconFile;
    private final JLabel iconLabel;
    private final JLabel lbl;
    private final Component spacer;

    // Tunables: clamp icon size between these bounds
    private static final int MIN_ICON_PX = 48;
    private static final int MAX_ICON_PX = 88;

    // Gap between image and text
    private static final int ICON_TEXT_GAP = 10;

    // Slight font size bump for service labels (smaller than the pickup button)
    private static final float LABEL_FONT_DELTA = 1.0f;

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
        add(spacer);
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
        SwingUtilities.invokeLater(() -> {
            applyLabelFont();
            updateIconSize();
        });
    }

    @Override
    public void updateUI() {
        super.updateUI();
        // Re-apply label font on LAF changes
        if (lbl != null) {
            applyLabelFont();
        }
    }

    private void applyLabelFont() {
        // Base from UI or label; increase slightly
        Font base = UIManager.getFont("defaultFont");
        if (base == null) base = lbl.getFont();
        float size = (base != null ? base.getSize2D() : 12f) + LABEL_FONT_DELTA;

        // Try Fredoka bold; fallback to base bold
        Font fredokaBold = getFredokaBold(size, (base != null ? base : new Font("Dialog", Font.PLAIN, Math.round(size))));
        lbl.setFont(fredokaBold);
        Color fg = UIManager.getColor("Label.foreground");
        if (fg != null) lbl.setForeground(fg);
        revalidate();
        repaint();
    }

    private static Font getFredokaBold(float size, Font fallback) {
        String[] candidates = { "Fredoka", "Fredoka Medium", "Fredoka One" };
        for (String name : candidates) {
            Font f = new Font(name, Font.BOLD, Math.round(size));
            if (!"Dialog".equalsIgnoreCase(f.getFamily())) {
                return f.deriveFont(Font.BOLD, size);
            }
        }
        return fallback.deriveFont(Font.BOLD, size);
    }

    private void updateIconSize() {
        int w = Math.max(0, getWidth());
        int h = Math.max(0, getHeight());
        if (w == 0 || h == 0) return;

        int labelH = lbl.getPreferredSize().height + ICON_TEXT_GAP;
        int availHForIcon = Math.max(0, h - labelH - 8);

        // Conservative responsive scaling
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