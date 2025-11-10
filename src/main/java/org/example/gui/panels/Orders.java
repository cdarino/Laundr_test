package org.example.gui.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import org.example.database.CustomerDAO;
import org.example.database.DBConnect;
import org.example.database.OrderDAO;
import org.example.gui.Mainframe;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;
import java.util.List;

import org.example.gui.utils.orders.orderCard;
import org.example.gui.utils.orders.orderStateButton;

/**
 * Original Orders panel design (restored).
 * Shows toggle buttons (Ongoing / Completed) and switches a CardLayout
 * between two scrollable containers.
 *
 * Data loads:
 * - whenever setVisible(true) is called (navigation to ORDERS card)
 * - via the new public refresh() method if you prefer explicit triggering.
 */
public class Orders extends JPanel {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel ongoingContainer;
    private JPanel completedContainer;

    private Mainframe frame;
    private OrderDAO orderDAO;
    private CustomerDAO customerDAO;
    private int currentCustID = -1; // cache the customer id
    private orderStateButton ongoingBtn;
    private orderStateButton completedBtn;

    public Orders(Mainframe frame) {
        this.frame = frame;

        // --- initialize daos ---
        try {
            Connection conn = DBConnect.getConnection();
            if (conn != null && !conn.isClosed()) {
                this.orderDAO = new OrderDAO(conn);
                this.customerDAO = new CustomerDAO(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JPanel orderState = new JPanel();
        orderState.setLayout(new BoxLayout(orderState, BoxLayout.X_AXIS));
        orderState.setOpaque(false);

        ongoingBtn = new orderStateButton("Ongoing", () -> showCard("ongoing"));
        completedBtn = new orderStateButton("Completed", () -> showCard("completed"));

        // default pressed set to ongoing
        SwingUtilities.invokeLater(() -> ongoingBtn.doClick());

        orderState.add(Box.createHorizontalStrut(60));
        orderState.add(ongoingBtn);
        orderState.add(Box.createHorizontalStrut(20));
        orderState.add(completedBtn);
        orderState.add(Box.createHorizontalStrut(60));

        topPanel.add(Box.createVerticalStrut(30));
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

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 60));
        contentWrapper.add(cardPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(contentWrapper, BorderLayout.CENTER);
    }

    // load all order data ---
    private void loadOrderData() {
        if (orderDAO == null || customerDAO == null) {
            System.err.println("orderspanel: daos not initialized.");
            return;
        }

        try {
            String username = frame.getCurrentUser();
            if (username == null) {
                // not logged in
                ongoingContainer.removeAll();
                completedContainer.removeAll();
                ongoingContainer.repaint();
                completedContainer.repaint();
                return;
            }

            // get custid once
            if (currentCustID == -1) { // only fetch if we don't have it
                currentCustID = customerDAO.getCustomerId(username);
            }

            if (currentCustID == -1) {
                System.err.println("orderspanel: could not find custid for " + username);
                return;
            }

            // clear old data
            ongoingContainer.removeAll();
            completedContainer.removeAll();

            // define statuses
            List<String> ongoingStatuses = List.of("pending", "accepted", "in_progress", "ready_for_delivery", "out_for_delivery");
            List<String> completedStatuses = List.of("completed");

            // fetch ongoing orders
            Vector<Vector<Object>> ongoingData = orderDAO.getDynamicOrders(currentCustID, ongoingStatuses, "ASC");
            for (Vector<Object> row : ongoingData) {
                // o.orderID, l.laundromatName, l.laundromatAddress, o.totalAmount, o.orderDate, o.orderStatus
                addOngoingOrder(
                        "#" + row.get(0).toString(),
                        (String) row.get(1),
                        (String) row.get(2),
                        "₱" + row.get(3).toString(),
                        row.get(4).toString(),
                        (String) row.get(5)
                );
            }

            // fetch completed orders
            Vector<Vector<Object>> completedData = orderDAO.getDynamicOrders(currentCustID, completedStatuses, "DESC");
            for (Vector<Object> row : completedData) {
                addCompletedOrder(
                        "#" + row.get(0).toString(),
                        (String) row.get(1),
                        (String) row.get(2),
                        "₱" + row.get(3).toString(),
                        row.get(4).toString(),
                        (String) row.get(5)
                );
            }

            // OPTIONAL: show placeholder if empty (commented to preserve exact original visual behavior)
            /*
            if (ongoingData.isEmpty()) {
                ongoingContainer.add(makePlaceholder("No ongoing orders."));
            }
            if (completedData.isEmpty()) {
                completedContainer.add(makePlaceholder("No completed orders."));
            }
            */

            // refresh ui
            ongoingContainer.revalidate();
            ongoingContainer.repaint();
            completedContainer.revalidate();
            completedContainer.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // public method to allow explicit refresh calls (added)
    public void refresh() {
        // force re-fetch cust id on explicit refresh if needed
        loadOrderData();
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            // refresh data every time panel is shown
            loadOrderData();
        } else {
            // when panel is hidden, reset custid so next show re-resolves
            currentCustID = -1;
        }
    }

    public void addOngoingOrder(String id, String shop, String address, String price, String date, String status) {
        orderCard card = new orderCard(id, shop, address, price, date, status);
        ongoingContainer.add(card);
        ongoingContainer.add(Box.createVerticalStrut(10));
    }

    public void addCompletedOrder(String id, String shop, String address, String price, String date, String status) {
        orderCard card = new orderCard(id, shop, address, price, date, status);
        completedContainer.add(card);
        completedContainer.add(Box.createVerticalStrut(10));
    }

    private void showCard(String name) {
        cardLayout.show(cardPanel, name);
    }

    @Override
    public void updateUI() {
        super.updateUI();
    }

    // Optional placeholder helper (commented out usage to preserve original visuals)
    private JComponent makePlaceholder(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIManager.getFont("Label.font"));
        lbl.setForeground(UIManager.getColor("Label.disabledForeground"));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(6, 6, 6, 6));
        return lbl;
    }
}