package org.example.gui.utils.orders;

import org.example.gui.utils.creators.roundedPanel;

import javax.swing.*;
import java.awt.*;

public class orderStateButton extends roundedPanel {
    private final JLabel label;
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 60;
    private static orderStateButton activeButton;
    private final Runnable action;

    public orderStateButton(String text, Runnable action) {
        this.action = action;

        setLayout(new GridBagLayout());
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setOpaque(false);

        setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, BUTTON_HEIGHT));
        setMinimumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        setAlignmentX(Component.CENTER_ALIGNMENT);

        label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setIconTextGap(0);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        add(label, gbc);

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                setActive();
                if (action != null) action.run();
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (orderStateButton.this != activeButton) {
                    setBackground(UIManager.getColor("Button.pressedBackground"));
                    repaint();
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (orderStateButton.this != activeButton) {
                    setBackground(UIManager.getColor("Sidebarbtn.background"));
                    repaint();
                }
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                setBackground(UIManager.getColor("Sidebar.hoverBackground"));
                repaint();
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (orderStateButton.this != activeButton) {
                    setBackground(UIManager.getColor("Sidebarbtn.background"));
                }
                repaint();
            }
        });

        updateUI();
    }

    public void doClick() {
        setActive();
        action.run();
    }

    private void setActive() {
        if (activeButton != null && activeButton != this) {
            activeButton.setBackground(UIManager.getColor("Sidebarbtn.background"));
            activeButton.repaint();
        }
        activeButton = this;
        setBackground(UIManager.getColor("Sidebar.hoverBackground"));
        repaint();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (label != null) {
            Font f = UIManager.getFont("Button.font");
            if (f != null) label.setFont(f);
            Color fg = UIManager.getColor("Button.foreground");
            if (fg != null) label.setForeground(fg);
        }

        if (this == activeButton) {
            setBackground(UIManager.getColor("Button.pressedBackground"));
        } else {
            Color bg = UIManager.getColor("Sidebarbtn.background");
            if (bg != null) setBackground(bg);
        }
    }
}
