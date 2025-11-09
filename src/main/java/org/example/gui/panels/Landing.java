package org.example.gui.panels;

import org.example.gui.Mainframe;
import org.example.gui.utils.creators.headerCreator;
import org.example.gui.utils.sidebar.sidebarFactory;
import org.example.gui.utils.notifications.NotificationsPanel;

import javax.swing.*;
import java.awt.*;

public class Landing extends JPanel {

    private headerCreator header;
    private sidebarFactory sidebar;
    private CardLayout centerLayout;
    private JPanel centerPanel;
    private NotificationsPanel notificationsPanel;

    public Landing(Mainframe frame) {
        setLayout(new BorderLayout());
        setBackground(UIManager.getColor("Panel.background"));

        // pass a callback that toggles the sidebar
        header = new headerCreator(frame, this::toggleSidebar, this::toggleNotifications);
        add(header, BorderLayout.NORTH);

        centerLayout = new CardLayout();
        centerPanel = new JPanel(centerLayout);

        // adding cards to landing
        centerPanel.add(new Dashboard(frame), "DASHBOARD");
        centerPanel.add(new Profile(frame, this), "PROFILE");
        centerPanel.add(new Laundromats(), "LAUNDROMATS");
        centerPanel.add(new EditProfile(frame, this), "EDIT");
        centerPanel.add(new DigitalWallet(frame, this), "WALLET");
        centerPanel.add(new ToReceive(frame, this), "RECEIVE");
        centerPanel.add(new ToRate(frame, this), "RATE");
        centerPanel.add(new PickupPanel(), "PICKUP");
        centerPanel.add(new Orders(frame), "ORDERS");

        add(centerPanel, BorderLayout.CENTER);

        sidebar = new sidebarFactory(this, frame);
        add(sidebar, BorderLayout.WEST);

        notificationsPanel = new NotificationsPanel(frame, this);
        add(notificationsPanel, BorderLayout.EAST);

        centerLayout.show(centerPanel, "DASHBOARD");
    }

    public void showCard(String name) {
        centerLayout.show(centerPanel, name);
    }

    /**
     * Called by header's burger button.
     */
    public void toggleSidebar() {
        if (sidebar != null) {
            sidebar.toggle();
            revalidate();
            repaint();
        }
    }

    public void toggleNotifications() {
        if (notificationsPanel != null) {
            notificationsPanel.toggle();

            // if we are opening notifications, close the sidebar
            if (!notificationsPanel.isCollapsed() && sidebar != null && !sidebar.isCollapsed()) {
                sidebar.setCollapsed(true);
            }

            revalidate();
            repaint();
        }
    }

    public void showDashboard() {
        centerLayout.show(this, "DASHBOARD");
    }

    public void showProfile() {
        centerLayout.show(this, "PROFILE");
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setBackground(UIManager.getColor("Panel.background"));
        if (header != null) header.updateUI();
        if (sidebar != null) sidebar.updateUI();
        if (centerPanel != null) centerPanel.updateUI();
    }
}
