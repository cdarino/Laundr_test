package org.example.gui.panels;

import org.example.gui.Mainframe;
import org.example.gui.utils.dashboard.recommendations.availableLaundromats;
import org.example.gui.utils.dashboard.recommendations.recentOrders;
import org.example.gui.utils.dashboard.welcomeCard;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JPanel {
    private static final int TOP_MARGIN = 40;
    private static final int SIDE_MARGIN = 40;

    private welcomeCard welcomeCard;
    private availableLaundromats leftPanel;
    private recentOrders rightPanel;

    private final Mainframe frame;

    private JPanel mainWrapper;

    public Dashboard(Mainframe frame) {
        this.frame = frame;
        setOpaque(true);
        setBackground(UIManager.getColor("background"));
        setLayout(new BorderLayout());

        initializeComponents();
    }

    private void initializeComponents() {
        mainWrapper = new JPanel();
        mainWrapper.setOpaque(false);
        mainWrapper.setLayout(new BoxLayout(mainWrapper, BoxLayout.Y_AXIS));
        mainWrapper.setBorder(BorderFactory.createEmptyBorder(TOP_MARGIN, SIDE_MARGIN, 40, SIDE_MARGIN));

        String username = frame.getCurrentUser();
        welcomeCard = new welcomeCard(username);
        welcomeCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        welcomeCard.setMinimumSize(new Dimension(Integer.MAX_VALUE, 180));
        mainWrapper.add(welcomeCard);
        mainWrapper.add(Box.createVerticalStrut(20));

        JPanel recommendationsPanel = new JPanel();
        recommendationsPanel.setOpaque(false);
        recommendationsPanel.setLayout(new BoxLayout(recommendationsPanel, BoxLayout.X_AXIS));

        leftPanel = new availableLaundromats(frame);
        leftPanel.setPreferredSize(new Dimension(300, 400));
        leftPanel.setMinimumSize(new Dimension(200, 200));
        leftPanel.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));

        rightPanel = new recentOrders(frame);
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
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag && rightPanel != null && welcomeCard != null) {
            // Use invokeLater to ensure the UI has completed rendering before fetching orders
            SwingUtilities.invokeLater(() -> {
                String username = frame.getCurrentUser();
                welcomeCard.updateUser(username);
                rightPanel.loadRecentOrders();  // Load recent orders after the panel is fully visible
            });
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setBackground(UIManager.getColor("background"));
        if (mainWrapper != null) {
            mainWrapper.setBackground(UIManager.getColor("background"));
        }
    }
}