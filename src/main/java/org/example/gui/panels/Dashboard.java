package org.example.gui.panels;

import org.example.gui.Mainframe;
// --- your new file uses this path ---
import org.example.gui.utils.dashboard.recommendations.availableLaundromats;
// ---
import org.example.gui.utils.dashboard.recommendations.recentOrders;
import org.example.gui.utils.dashboard.welcomeCard;

import javax.swing.*;
import java.awt.*;
// --- import the timer for the fix ---
import javax.swing.Timer;

public class Dashboard extends JPanel {
    private static final int TOP_MARGIN = 40;
    private static final int SIDE_MARGIN = 40;

    private welcomeCard welcomeCard;
    // --- your new file uses this type ---
    private availableLaundromats leftPanel;
    // ---
    private recentOrders rightPanel;

    private final Mainframe frame;

    private JPanel mainWrapper;

    // --- add a timer to prevent the load from triggering too early ---
    private Timer loadTimer;

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

        // --- your new file uses this constructor (passing frame) ---
        leftPanel = new availableLaundromats(frame);
        // ---
        leftPanel.setPreferredSize(new Dimension(300, 400));
        leftPanel.setMinimumSize(new Dimension(200, 200));
        leftPanel.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));

        // --- your new file uses this constructor (passing frame) ---
        rightPanel = new recentOrders(frame);
        // ---
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
        // when the dashboard becomes visible (e.g., after login),
        // we need to tell the child panels to load their data.
        if (aFlag && rightPanel != null && welcomeCard != null) {

            // --- the fix ---
            // stop any previous timer that might be running
            if (loadTimer != null && loadTimer.isRunning()) {
                loadTimer.stop();
            }

            // we use a short timer instead of running the code directly.
            // this gives the cardlayout 50ms to fully "settle"
            // before we load data and trigger the repaint.
            // this fixes the race condition.
            loadTimer = new Timer(50, (e) -> {
                // get the user that just logged in
                String username = frame.getCurrentUser();

                // 1. update the welcome card with the user's name
                welcomeCard.updateUser(username);

                // 2. tell the recent orders panel to fetch data from the db
                rightPanel.loadRecentOrders();

                // (availablelaundromats loads its own data in its constructor, so it's fine)
            });
            loadTimer.setRepeats(false); // only run once
            loadTimer.start();
            // --- end fix ---
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