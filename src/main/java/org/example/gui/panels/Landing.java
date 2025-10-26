package org.example.gui.panels;

import org.example.gui.Mainframe;
import org.example.gui.utils.creators.headerCreator;
import org.example.gui.utils.sidebar.sidebarFactory;

import javax.swing.*;
import java.awt.*;

public class Landing extends JPanel {

    private headerCreator header;
    private sidebarFactory sidebar;
    private CardLayout centerLayout;
    private JPanel centerPanel;

    public Landing(Mainframe frame) {
        setLayout(new BorderLayout());
        setBackground(UIManager.getColor("Panel.background"));

        // pass a callback that toggles the sidebar
        header = new headerCreator(frame, this::toggleSidebar);
        add(header, BorderLayout.NORTH);

        centerLayout = new CardLayout();
        centerPanel = new JPanel(centerLayout);

        centerPanel.add(new Dashboard(), "DASHBOARD");
        centerPanel.add(new Profile(), "PROFILE");
        centerPanel.add(new Profile(), "PROFILE");
        centerPanel.add(new EditProfile(), "EDIT");
        centerPanel.add(new Orders(), "ORDERS");

        add(centerPanel, BorderLayout.CENTER);

        sidebar = new sidebarFactory(this);
        add(sidebar, BorderLayout.WEST);

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
