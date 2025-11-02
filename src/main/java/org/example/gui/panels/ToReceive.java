package org.example.gui.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.example.gui.utils.fonts.fontManager;
import org.example.gui.utils.orders.toReceiveCard;

public class ToReceive extends JPanel {
    private JLabel titleLabel;
    private JLabel backLabel; // <-- CHANGED: Made backLabel a class field
    private JPanel containerPanel;
    private Landing landing;

    public ToReceive(Landing landing) {
        this.landing = landing;

        // Call initComponents to set up the UI
        initComponents();
    }

    private void initComponents() {
        // Clear all components before rebuilding (good for updateUI)
        removeAll();

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 0, 40));

        // 1. Back Button
        backLabel = new JLabel("< Back"); // <-- Use the class field
        fontManager.applyHeading(backLabel, 8);
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (ToReceive.this.landing != null) {
                    ToReceive.this.landing.showCard("PROFILE");
                } else {
                    System.out.println("Landing reference is null. Cannot navigate.");
                }
                backLabel.requestFocusInWindow();
            }
        });
        topPanel.add(backLabel);
        topPanel.add(Box.createVerticalStrut(20));

        // 2. Title
        titleLabel = new JLabel("To Receive");
        fontManager.applyHeading(titleLabel, 5);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(20));

        // 3. Scrollable Content Area (Container setup remains the same)
        containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setOpaque(false);

        containerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        containerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JScrollPane scrollPane = new JScrollPane(containerPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 60));
        contentWrapper.add(scrollPane, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(contentWrapper, BorderLayout.CENTER);

        addToReceiveCard("007", "5 kg", "Best Laundry Shop",
                "Corner of Magallanes & C.M. Recto, Davao City",
                "₱450.00", "5 hours left");

        addToReceiveCard("007", "5 kg", "Best Laundry Shop",
                "Corner of Magallanes & C.M. Recto, Davao City",
                "₱450.00", "5 hours left");

        addToReceiveCard("007", "5 kg", "Best Laundry Shop",
                "Corner of Magallanes & C.M. Recto, Davao City",
                "₱450.00", "5 hours left");

        addToReceiveCard("007", "5 kg", "Best Laundry Shop",
                "Corner of Magallanes & C.M. Recto, Davao City",
                "₱450.00", "5 hours left");

        addToReceiveCard("007", "5 kg", "Best Laundry Shop",
                "Corner of Magallanes & C.M. Recto, Davao City",
                "₱450.00", "5 hours left");

        // layout update
        revalidate();
        repaint();
    }

    public void addToReceiveCard(String id, String weight, String shop, String address, String price, String eta) {
        toReceiveCard card = new toReceiveCard(id, weight, shop, address, price, eta);
        containerPanel.add(card);
        containerPanel.add(Box.createVerticalStrut(10));
        containerPanel.revalidate();
        containerPanel.repaint();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        SwingUtilities.invokeLater(() -> {
            if (titleLabel != null) {
                fontManager.applyHeading(titleLabel, 5);
            }
            if (backLabel != null) {
                fontManager.applyHeading(backLabel, 8);
            }
            repaint();
        });
    }
}