package org.example.gui.panels;

import org.example.gui.Mainframe;
import org.example.gui.utils.dashboard.recommendations.laundromatsNearUser;
import org.example.gui.utils.dashboard.recommendations.recentOrders;
import org.example.gui.utils.dashboard.welcomeCard;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JPanel {
    private static final int TOP_MARGIN = 40;
    private static final int SIDE_MARGIN = 40;

    private welcomeCard welcomeCard;
    private laundromatsNearUser leftPanel;
    private recentOrders rightPanel;

    private JLabel usernameLabel;

    public Dashboard() {
        setOpaque(true);
        setBackground(UIManager.getColor("background"));
        setLayout(new BorderLayout());

        initializeComponents();
    }

    private void initializeComponents() {
        JPanel mainWrapper = new JPanel();
        mainWrapper.setOpaque(false);
        mainWrapper.setLayout(new BoxLayout(mainWrapper, BoxLayout.Y_AXIS));
        mainWrapper.setBorder(BorderFactory.createEmptyBorder(TOP_MARGIN, SIDE_MARGIN, 40, SIDE_MARGIN));

        welcomeCard = new welcomeCard("username");
        welcomeCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        mainWrapper.add(welcomeCard);
        mainWrapper.add(Box.createVerticalStrut(20));

        JPanel recommendationsPanel = new JPanel();
        recommendationsPanel.setOpaque(false);
        recommendationsPanel.setLayout(new BoxLayout(recommendationsPanel, BoxLayout.X_AXIS));

        leftPanel = new laundromatsNearUser();
        leftPanel.setPreferredSize(new Dimension(300, 400));
        leftPanel.setMinimumSize(new Dimension(200, 200));
        leftPanel.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));

        rightPanel = new recentOrders();
        rightPanel.setPreferredSize(new Dimension(600, 400));
        rightPanel.setMinimumSize(new Dimension(200, 200));
        rightPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        recommendationsPanel.add(leftPanel);
        recommendationsPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        recommendationsPanel.add(rightPanel);

        recommendationsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainWrapper.add(recommendationsPanel);

        add(mainWrapper, BorderLayout.CENTER);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setBackground(UIManager.getColor("background"));
        if (welcomeCard != null) welcomeCard.updateUI();
        repaint();
    }

    public void setUsername(String username) {
        usernameLabel.setText("Welcome, " + username + "!");
    }
}
