package org.example.gui.utils.creators;

import javax.swing.*;
import java.awt.*;

import org.example.gui.Mainframe;

/**
 * Header with logo, burger menu (to toggle sidebar) and theme toggle.
 * Uses a Runnable callback for sidebar toggling and provides the same
 * hover/pressed UX as other interactive controls.
 */
public class headerCreator extends JPanel {
    private final Mainframe frame;
    private final Runnable toggleSidebarAction;
    private final JButton burgerBtn;
    private final JButton themeToggleBtn;
    private final JLabel logoLabel;
    private final Runnable toggleNotificationsAction;
    private final JButton notificationBtn;

    public headerCreator(Mainframe frame, Runnable toggleSidebarAction, Runnable toggleNotificationsAction) {
        this.frame = frame;
        this.toggleSidebarAction = toggleSidebarAction;
        this.toggleNotificationsAction = toggleNotificationsAction;

        setPreferredSize(new Dimension(0, 60));
        setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);

        // Burger button (toggle)
        burgerBtn = new JButton();
        burgerBtn.setPreferredSize(new Dimension(44, 44));
        burgerBtn.setFocusPainted(false);
        burgerBtn.setBorderPainted(false);
        burgerBtn.setContentAreaFilled(true);
        burgerBtn.setOpaque(true);
        burgerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Action
        burgerBtn.addActionListener(e -> {
            if (toggleSidebarAction != null) toggleSidebarAction.run();
        });

        // Hover / pressed behavior — mirror other interactive components (dark mode)
        burgerBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            private boolean inside = false;

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                inside = true;
                burgerBtn.setBackground(resolveHoverColor());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                inside = false;
                burgerBtn.setBackground(resolveBaseColor());
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                burgerBtn.setBackground(resolvePressedColor());
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                // keep hover color if still inside, otherwise reset
                burgerBtn.setBackground(inside ? resolveHoverColor() : resolveBaseColor());
            }
        });

        leftPanel.add(burgerBtn);

        logoLabel = new JLabel(iconCreator.getIcon("Icons/logos/logoWhite.svg", 70, 50));
        leftPanel.add(logoLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);

        themeToggleBtn = new themeToggleButton(frame::toggleTheme);
        rightPanel.add(themeToggleBtn);

        notificationBtn = new JButton();
        notificationBtn.setPreferredSize(new Dimension(44, 44));
        notificationBtn.setFocusPainted(false);
        notificationBtn.setBorderPainted(false);
        notificationBtn.setContentAreaFilled(true);
        notificationBtn.setOpaque(true);
        notificationBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        notificationBtn.addActionListener(e -> {
            if (toggleNotificationsAction != null) toggleNotificationsAction.run();
        });

        // mouse behavior
        notificationBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            private boolean inside = false;
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                inside = true;
                notificationBtn.setBackground(resolveHoverColor());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                inside = false;
                notificationBtn.setBackground(resolveBaseColor());
            }
        });

        rightPanel.add(notificationBtn);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);

        updateUI();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setBackground(UIManager.getColor("Menu.background"));
        setBorder(BorderFactory.createMatteBorder(
                0, 0, 2, 0,
                UIManager.getColor("Menu.borderColor")
        ));
        setForeground(UIManager.getColor("Menu.foreground"));

        if (themeToggleBtn != null) {
            themeToggleBtn.setFont(UIManager.getFont("Button.font"));
            Color fg = UIManager.getColor("Button.foreground");
            if (fg != null) themeToggleBtn.setForeground(fg);
        }

        if (logoLabel != null) {
            logoLabel.setIcon(iconCreator.getIcon(
                    frame.isDarkMode() ? "Icons/logos/logoDarkMode.svg" : "Icons/logos/logoWhite.svg",
                    70, 50
            ));
        }

        if (burgerBtn != null) {
            burgerBtn.setIcon(iconCreator.getIcon("Icons/burgerMenu.svg", 28, 28));
            burgerBtn.setBackground(resolveBaseColor());
            burgerBtn.setToolTipText("Toggle sidebar");
        }

        if (notificationBtn != null) {
            notificationBtn.setIcon(iconCreator.getIcon("Icons/notification.svg", 24, 24));
            notificationBtn.setBackground(resolveBaseColor());
            notificationBtn.setToolTipText("Toggle notifications");
        }
    }

    /**
     * Try UIManager keys used by other components first; fall back to a derived darker variant.
     */
    private Color resolveBaseColor() {
        Color c = UIManager.getColor("Menu.background");
        if (c != null) return c;
        c = UIManager.getColor("Panel.background");
        if (c != null) return c;
        return UIManager.getColor("Button.background") != null ? UIManager.getColor("Button.background") : Color.WHITE;
    }

    private Color resolveHoverColor() {
        Color c = UIManager.getColor("Button.hoverBackground");
        if (c != null) return c;
        c = UIManager.getColor("Sidebar.hoverBackground");
        if (c != null) return c;
        // fallback: slightly darker than base
        return darker(resolveBaseColor(), 0.08f);
    }

    private Color resolvePressedColor() {
        Color c = UIManager.getColor("Button.pressedBackground");
        if (c != null) return c;
        // fallback: a bit darker than hover
        return darker(resolveHoverColor(), 0.10f);
    }

    private Color darker(Color in, float fraction) {
        if (in == null) return Color.LIGHT_GRAY;
        int r = (int) Math.max(0, in.getRed() * (1 - fraction));
        int g = (int) Math.max(0, in.getGreen() * (1 - fraction));
        int b = (int) Math.max(0, in.getBlue() * (1 - fraction));
        return new Color(r, g, b);
    }
}
