package org.example.gui.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.example.database.CustomerDAO;
import org.example.database.DBConnect;
import org.example.database.OrderDAO;
import org.example.gui.Mainframe;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;
import java.util.List;

import org.example.gui.utils.fonts.fontManager;
import org.example.gui.utils.orders.orderCard;

public class ToReceive extends JPanel {
    private JLabel titleLabel;
    private JLabel backLabel;
    private JPanel containerPanel;
    private Landing landing;

    private Mainframe frame;
    private OrderDAO orderDAO;
    private CustomerDAO customerDAO;
    private int currentCustID = -1;

    public ToReceive(Mainframe frame, Landing landing) {
        this.frame = frame; // store mainframe
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
                if (landing != null) {
                    landing.showCard("PROFILE");
                }
            }
        });

        // 2. Title
        titleLabel = new JLabel("To Receive");
        fontManager.applyHeading(titleLabel, 5);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        topPanel.add(backLabel);
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(10));

        // 3. Container Panel (for cards)
        containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setOpaque(false);

        // 4. Scroll Pane
        JScrollPane scrollPane = new JScrollPane(containerPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        // 5. Content Wrapper
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 60));
        contentWrapper.add(scrollPane, BorderLayout.CENTER);

        // add main panels
        add(topPanel, BorderLayout.NORTH);
        add(contentWrapper, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    // load data
    private void loadToReceiveData() {
        if (orderDAO == null || customerDAO == null) {
            System.err.println("toreceivepanel: daos not initialized.");
            return;
        }

        containerPanel.removeAll();

        try {
            String username = frame.getCurrentUser();
            if (username == null) return;

            if (currentCustID == -1) {
                currentCustID = customerDAO.getCustomerId(username);
            }

            if (currentCustID == -1) {
                System.err.println("toreceivepanel: could not find custid for " + username);
                return;
            }

            // --- statuses for "To Receive" ---
            List<String> ongoingStatuses = List.of("pending", "accepted",
                    "in_progress", "ready_for_delivery", "out_for_delivery");

            Vector<Vector<Object>> data = orderDAO.getDynamicOrders(currentCustID, ongoingStatuses, "DESC");

            if (data.isEmpty()) {
                System.out.println("No 'to receive' orders found.");
            } else {
                for (Vector<Object> row : data) {
                    String services = (row.get(6) != null) ? row.get(6).toString() : "No services listed";
                    addToReceiveCard(
                            row.get(0).toString(),
                            (String) row.get(1),
                            (String) row.get(2),
                            "₱" + row.get(3).toString(),
                            row.get(4).toString(),
                            (String) row.get(5),
                            services
                    );
                }
            }

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

    // uses new ordercard and passes status
    public void addToReceiveCard(String id, String shop, String address, String price, String date, String status, String servicesList) {
        orderCard card = new orderCard(id, shop, address, price, date, status, servicesList);
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