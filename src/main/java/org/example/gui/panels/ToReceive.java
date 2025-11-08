package org.example.gui.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
// new imports
import org.example.database.CustomerDAO;
import org.example.database.DBConnect;
import org.example.database.OrderDAO;
import org.example.gui.Mainframe;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;
import java.util.List;
// ---

import org.example.gui.utils.fonts.fontManager;
import org.example.gui.utils.orders.toReceiveCard;

public class ToReceive extends JPanel {
    private JLabel titleLabel;
    private JLabel backLabel; // <-- CHANGED: Made backLabel a class field
    private JPanel containerPanel;
    private Landing landing;

    // --- new fields for db connection ---
    private Mainframe frame;
    private OrderDAO orderDAO;
    private CustomerDAO customerDAO;
    private int currentCustID = -1;
    // ---

    // --- UPDATED CONSTRUCTOR ---
    public ToReceive(Mainframe frame, Landing landing) {
        this.frame = frame;
        this.landing = landing;

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
        // ---

        // Call initComponents to set up the UI
        initComponents();
    }

    private void initComponents() {
        // Clear all components before rebuilding (good for updateUI)
        removeAll();

        setLayout(new BorderLayout());
        setOpaque(false); // make panel transparent

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
                // Use the landing reference to go back
                if (landing != null) {
                    landing.showCard("PROFILE");
                }
            }
        });

        // 2. Title
        titleLabel = new JLabel("To Receive");
        fontManager.applyHeading(titleLabel, 5);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add some space

        topPanel.add(backLabel);
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(10));

        // 3. Container for Cards
        containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setOpaque(false);

        // 4. Scroll Pane
        JScrollPane scrollPane = new JScrollPane(containerPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false); // scrollpane transparent
        scrollPane.getViewport().setOpaque(false); // viewport transparent
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false); // wrapper transparent
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 60));
        contentWrapper.add(scrollPane, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(contentWrapper, BorderLayout.CENTER);

        // --- remove all hardcoded data ---
        // (they are now in loadToReceiveData)

        // layout update
        revalidate();
        repaint();
    }

    // --- new method to load data ---
    private void loadToReceiveData() {
        if (orderDAO == null || customerDAO == null) {
            System.err.println("toreceivepanel: daos not initialized.");
            return;
        }

        // clear old data
        containerPanel.removeAll();

        try {
            String username = frame.getCurrentUser();
            if (username == null) { return; } // not logged in

            if (currentCustID == -1) {
                currentCustID = customerDAO.getCustomerId(username);
            }

            if (currentCustID == -1) {
                System.err.println("toreceivepanel: could not find custid for " + username);
                return;
            }

            // fetch ongoing orders (same as in orders.java)
            List<String> ongoingStatuses = List.of("pending", "accepted", "in_progress", "ready_for_delivery", "out_for_delivery");
            Vector<Vector<Object>> ongoingData = orderDAO.getDynamicOrders(currentCustID, ongoingStatuses, "ASC");

            if (ongoingData.isEmpty()) {
                containerPanel.add(new JLabel("You have no orders to receive."));
            } else {
                for (Vector<Object> row : ongoingData) {
                    // o.orderID, l.laundromatName, l.laundromatAddress, o.totalAmount, o.orderDate
                    addToReceiveCard(
                            "#" + row.get(0).toString(),
                            (String) row.get(1), // laundromatname
                            (String) row.get(2), // laundromataddress
                            "₱" + row.get(3).toString(),
                            row.get(4).toString() // date (for the 'eta' slot)
                    );
                }
            }

            // refresh ui
            containerPanel.revalidate();
            containerPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- new method to refresh data when panel is shown ---
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            // refresh data every time panel is shown
            loadToReceiveData();
        } else {
            // when panel is hidden, reset custid
            currentCustID = -1;
        }
    }

    // updated addtoreceivecard to use your original card constructor
    public void addToReceiveCard(String id, String shop, String address, String price, String eta) {
        toReceiveCard card = new toReceiveCard(id, shop, address, price, eta);
        containerPanel.add(card);
        containerPanel.add(Box.createVerticalStrut(10));
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
        });
    }
}