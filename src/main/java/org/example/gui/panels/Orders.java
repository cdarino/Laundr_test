package org.example.gui.panels;

import javax.swing.*;
import java.awt.*;

import org.example.gui.utils.fonts.fontManager;
import org.example.gui.utils.orders.orderCard;
import org.example.gui.utils.orders.orderStateButton;

public class Orders extends JPanel {
    private JLabel myOrderLabel;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel ongoingContainer;
    private JPanel completedContainer;

    public Orders() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        myOrderLabel = new JLabel("My Orders");
        myOrderLabel.setHorizontalAlignment(SwingConstants.LEFT);
        fontManager.applyHeading(myOrderLabel, 3);

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.setOpaque(false);
        labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));
        labelPanel.add(myOrderLabel);

        JPanel orderState = new JPanel();
        orderState.setLayout(new BoxLayout(orderState, BoxLayout.X_AXIS));
        orderState.setOpaque(false);

        orderStateButton ongoingBtn = new orderStateButton("Ongoing", () -> showCard("ongoing"));
        orderStateButton completedBtn = new orderStateButton("Completed", () -> showCard("completed"));

        //default pressed set to ongoing
        SwingUtilities.invokeLater(() -> {
            ongoingBtn.setBackground(UIManager.getColor("Sidebar.hoverBackground"));
            try {
                java.lang.reflect.Field f = orderStateButton.class.getDeclaredField("activeButton");
                f.setAccessible(true);
                f.set(null, ongoingBtn);
            } catch (Exception ignored) {}
        });

        orderState.add(Box.createHorizontalStrut(60));
        orderState.add(ongoingBtn);
        orderState.add(Box.createHorizontalStrut(20));
        orderState.add(completedBtn);
        orderState.add(Box.createHorizontalStrut(60));

        topPanel.add(Box.createVerticalStrut(20));
        topPanel.add(labelPanel);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(orderState);
        topPanel.add(Box.createVerticalStrut(20));

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        ongoingContainer = new JPanel();
        ongoingContainer.setLayout(new BoxLayout(ongoingContainer, BoxLayout.Y_AXIS));
        ongoingContainer.setOpaque(false);
        JScrollPane ongoingScroll = new JScrollPane(ongoingContainer);
        ongoingScroll.setBorder(BorderFactory.createEmptyBorder());
        ongoingScroll.getVerticalScrollBar().setUnitIncrement(16);
        ongoingScroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        completedContainer = new JPanel();
        completedContainer.setLayout(new BoxLayout(completedContainer, BoxLayout.Y_AXIS));
        completedContainer.setOpaque(false);
        JScrollPane completedScroll = new JScrollPane(completedContainer);
        completedScroll.setBorder(BorderFactory.createEmptyBorder());
        completedScroll.getVerticalScrollBar().setUnitIncrement(16);
        completedScroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        cardPanel.add(ongoingScroll, "ongoing");
        cardPanel.add(completedScroll, "completed");

        cardLayout.show(cardPanel, "ongoing");

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 60));
        contentWrapper.add(cardPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(contentWrapper, BorderLayout.CENTER);

        addOngoingOrder("Order #001", "Real Laundromat", "Real Address in Davao City, Real St. 123, Building 456",
                "2 kg",
                "₱200.00", "10/10/2025");

        addOngoingOrder("Order #002", "Real Laundromat", "Real Address in Davao City, Real St. 123, Building 456",
                "2 kg",
                "₱200.00", "10/10/2025");

        addOngoingOrder("Order #004", "Real Laundromat", "Real Address in Davao City, Real St. 123, Building 456",
                "2 kg",
                "₱200.00", "10/10/2025");

        addOngoingOrder("Order #005", "Real Laundromat", "Real Address in Davao City, Real St. 123, Building 456",
                "2 kg",
                "₱200.00", "10/10/2025");

        addOngoingOrder("Order #006", "Real Laundromat", "Real Address in Davao City, Real St. 123, Building 456",
                "2 kg",
                "₱200.00", "10/10/2025");

        addCompletedOrder("Order #003", "Real Laundromat", "Real Address in Davao City, Real St. 123, Building 456",
                "2 kg",
                "₱200.00", "10/10/2025");
    }

    public void addOngoingOrder(String id, String shop, String address, String weight, String price, String date) {
        orderCard card = new orderCard(id, weight, shop, address, price, date);
        ongoingContainer.add(card);
        ongoingContainer.add(Box.createVerticalStrut(10));
        ongoingContainer.revalidate();
        ongoingContainer.repaint();
    }

    public void addCompletedOrder(String id, String shop, String address, String weight, String price, String date) {
        orderCard card = new orderCard(id, weight, shop, address, price, date);
        completedContainer.add(card);
        completedContainer.add(Box.createVerticalStrut(10));
        completedContainer.revalidate();
        completedContainer.repaint();
    }

    private void showCard(String name) {
        cardLayout.show(cardPanel, name);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (myOrderLabel != null) fontManager.applyHeading(myOrderLabel, 3);
        repaint();
    }
}
