package org.example.gui.panels;

import javax.swing.*;
import java.awt.*;
// new imports
import org.example.database.CustomerDAO;
import org.example.database.DBConnect;
import org.example.database.OrderDAO;
import org.example.gui.Mainframe;
// import org.example.models.Customer; // not needed
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;
import java.util.List; // import list
import java.text.SimpleDateFormat; // to format date
// ---

import org.example.gui.utils.fonts.fontManager;
import org.example.gui.utils.orders.orderCard;
import org.example.gui.utils.orders.orderStateButton;
// new import
// import org.example.gui.utils.orders.toReceiveCard; // not needed here

public class Orders extends JPanel {
    private JLabel myOrderLabel;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel ongoingContainer;
    private JPanel completedContainer;

    // --- new fields for db connection ---
    private Mainframe frame;
    private OrderDAO orderDAO;
    private CustomerDAO customerDAO;
    private int currentCustID = -1; // cache the customer id

    // --- UPDATED CONSTRUCTOR ---
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
            // you could show an error panel here
        }
        // ---

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
            // ongoingBtn.setBackground(UIManager.getColor("Sidebar.hoverBackground"));
            // try {
            //     java.lang.reflect.Field f = orderStateButton.class.getDeclaredField("activeButton");
            //     f.setAccessible(true);
            //     f.set(null, ongoingBtn);
            // } catch (Exception ignored) {}

            // safer way to set default
            ongoingBtn.doClick();
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

        // cardLayout.show(cardPanel, "ongoing"); // default is set by doClick()

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 60));
        contentWrapper.add(cardPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(contentWrapper, BorderLayout.CENTER);

        // --- remove hardcoded calls ---
        // addOngoingOrder(...)
        // addCompletedOrder(...)
    }

    // --- new method to load all order data ---
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
                // o.orderID, l.laundromatName, l.laundromatAddress, o.totalAmount, o.orderDate
                addOngoingOrder(
                        "#" + row.get(0).toString(),
                        (String) row.get(1), // laundromatname
                        (String) row.get(2), // laundromataddress
                        "₱" + row.get(3).toString(),
                        row.get(4).toString() // date
                );
            }

            // fetch completed orders
            Vector<Vector<Object>> completedData = orderDAO.getDynamicOrders(currentCustID, completedStatuses, "DESC");
            for (Vector<Object> row : completedData) {
                addCompletedOrder(
                        "#" + row.get(0).toString(),
                        (String) row.get(1), // laundromatname
                        (String) row.get(2), // laundromataddress
                        "₱" + row.get(3).toString(),
                        row.get(4).toString() // date
                );
            }

            // refresh ui
            ongoingContainer.revalidate();
            ongoingContainer.repaint();
            completedContainer.revalidate();
            completedContainer.repaint();

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
            loadOrderData();
        } else {
            // when panel is hidden, reset custid
            currentCustID = -1;
        }
    }

    // updated addongoingorder to use new card constructor
    public void addOngoingOrder(String id, String shop, String address, String price, String date) {
        orderCard card = new orderCard(id, shop, address, price, date);
        ongoingContainer.add(card);
        ongoingContainer.add(Box.createVerticalStrut(10));
    }

    // updated addcompletedorder to use new card constructor
    public void addCompletedOrder(String id, String shop, String address, String price, String date) {
        orderCard card = new orderCard(id, shop, address, price, date);
        completedContainer.add(card);
        completedContainer.add(Box.createVerticalStrut(10));
    }

    private void showCard(String name) {
        cardLayout.show(cardPanel, name);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        // re-apply fonts
        SwingUtilities.invokeLater(() -> {
            if (myOrderLabel != null) {
                fontManager.applyHeading(myOrderLabel, 3);
            }
        });
    }
}