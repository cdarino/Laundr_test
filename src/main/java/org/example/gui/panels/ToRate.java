package org.example.gui.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.example.database.CustomerDAO;
import org.example.database.DBConnect;
import org.example.database.OrderDAO;
import org.example.database.ReviewDAO;
import org.example.models.ReviewData;
import org.example.gui.Mainframe;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import org.example.gui.utils.fonts.fontManager;
import org.example.gui.utils.ratings.RatingCard;

public class ToRate extends JPanel {
    private JLabel titleLabel;
    private JLabel backLabel;
    private JPanel containerPanel; // holds the ratingcard components
    private JScrollPane scrollPane; // reference for scrolling fix
    private Landing landing;

    // --- new fields for db connection ---
    private Mainframe frame;
    private OrderDAO orderDAO;
    private ReviewDAO reviewDAO; // new dao
    private CustomerDAO customerDAO;
    private int currentCustID = -1;
    // ---

    public ToRate(Mainframe frame, Landing landing) { // accept mainframe
        this.frame = frame; // store mainframe
        this.landing = landing;

        try {
            Connection conn = DBConnect.getConnection();
            if (conn != null && !conn.isClosed()) {
                this.orderDAO = new OrderDAO(conn);
                this.reviewDAO = new ReviewDAO(conn); // init review dao
                this.customerDAO = new CustomerDAO(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        initComponents();
    }

    private void initComponents() {
        removeAll();
        setLayout(new BorderLayout());
        setOpaque(false); // make panel transparent

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 0, 40));

        // back button
        backLabel = new JLabel("< Back");
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

        // title
        titleLabel = new JLabel("To Rate");
        fontManager.applyHeading(titleLabel, 5);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        topPanel.add(backLabel);
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(10));

        // container panel
        containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setOpaque(false);

        // scroll pane
        scrollPane = new JScrollPane(containerPanel);
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

        // --- remove hardcoded calls ---
        // addRatingCard(...)

        revalidate();
        repaint();
    }

    // --- new method to load data ---
    private void loadToRateData() {
        if (orderDAO == null || customerDAO == null || reviewDAO == null) {
            System.err.println("toratepanel: daos not initialized.");
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
                System.err.println("toratepanel: could not find custid for " + username);
                return;
            }

            // 1. fetch all completed orders
            Vector<Vector<Object>> completedOrders = orderDAO.getAllCompletedOrders(currentCustID);

            if (completedOrders.isEmpty()) {
                containerPanel.add(new JLabel("You have no completed orders to rate."));
            } else {
                for (Vector<Object> row : completedOrders) {
                    int orderID = (Integer) row.get(0);
                    int laundromatID = (Integer) row.get(1);
                    String laundromatName = (String) row.get(2);

                    // 2. for each order, check if a review already exists
                    ReviewData existingReview = reviewDAO.getReviewByOrderId(orderID);

                    // 3. add the rating card
                    // pass all data, including the (possibly null) existing review
                    addRatingCard(
                            orderID,
                            laundromatID,
                            currentCustID,
                            laundromatName,
                            existingReview
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
            loadToRateData();
        } else {
            // when panel is hidden, reset custid
            currentCustID = -1;
        }
    }

    // updated addratingcard to use new card constructor
    public void addRatingCard(int orderID, int laundromatID, int custID, String laundromatName, ReviewData existingReview) {
        // the callback will be to reload this panel's data
        RatingCard card = new RatingCard(orderID, laundromatID, custID, laundromatName, existingReview, this::loadToRateData);
        containerPanel.add(card);
        containerPanel.add(Box.createVerticalStrut(10)); // spacer between cards
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
            // this is a good place to make sure the scrollpane bg is transparent
            if (scrollPane != null) {
                scrollPane.setOpaque(false);
                scrollPane.getViewport().setOpaque(false);
            }
            if (containerPanel != null) {
                containerPanel.setOpaque(false);
            }
        });
    }
}