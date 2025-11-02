package org.example.gui.utils.sidebar;

import org.example.gui.panels.Landing;
import org.example.gui.Mainframe;
import org.example.gui.utils.creators.iconCreator;

import javax.swing.*;
import java.awt.*;

/**
 * Sidebar that can be shown/hidden completely while retaining a fixed width.
 * Buttons are intentionally narrower than the sidebar and centered.
 */
public class sidebarFactory extends JPanel {

    // Keep a stable sidebar width (restore original UI width)
    private static final int SIDEBAR_WIDTH = 200;
    private boolean collapsed = false;

    public sidebarFactory(Landing landing, Mainframe frame) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(true);
        setBackground(UIManager.getColor("Menu.background"));

        // Make the sidebar keep its width when visible
        setPreferredSize(new Dimension(SIDEBAR_WIDTH, Integer.MAX_VALUE));
        setMaximumSize(new Dimension(SIDEBAR_WIDTH, Integer.MAX_VALUE));
        setMinimumSize(new Dimension(SIDEBAR_WIDTH, 0));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        topPanel.add(Box.createVerticalStrut(15));

        topPanel.add(new sidebarButtons("Home",
                iconCreator.getIcon("Icons/home.svg", 24, 24),
                () -> landing.showCard("DASHBOARD")));
        topPanel.add(Box.createVerticalStrut(15));

        topPanel.add(new sidebarButtons("Laundromats",
                iconCreator.getIcon("Icons/laundromats.svg", 24, 24),
                () -> landing.showCard("LAUNDROMATS")));
        topPanel.add(Box.createVerticalStrut(15));

        topPanel.add(new sidebarButtons("Profile",
                iconCreator.getIcon("Icons/userIcon.svg", 24, 24),
                () -> landing.showCard("PROFILE")));
        topPanel.add(Box.createVerticalStrut(15));

        topPanel.add(new sidebarButtons("Orders",
                iconCreator.getIcon("Icons/order.svg", 24, 24),
                () -> landing.showCard("ORDERS")));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);

        bottomPanel.add(Box.createVerticalGlue());

        bottomPanel.add(new sidebarButtons("Logout",
                iconCreator.getIcon("Icons/logout.svg", 24, 24),
                frame::logout));

        bottomPanel.add(Box.createVerticalStrut(10));

        add(topPanel);
        add(Box.createVerticalGlue());
        add(bottomPanel);
    }

    /**
     * Toggle collapse (hide/show).
     */
    public void toggle() {
        setCollapsed(!collapsed);
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
        // hide entirely when collapsed and restore preferred size when visible
        setVisible(!collapsed);
        revalidate();
        repaint();
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setBackground(UIManager.getColor("Menu.background"));
        setForeground(UIManager.getColor("Menu.foreground"));
        // Maintain preferred width on L&F change
        setPreferredSize(new Dimension(SIDEBAR_WIDTH, Integer.MAX_VALUE));
        setMaximumSize(new Dimension(SIDEBAR_WIDTH, Integer.MAX_VALUE));
    }
}
