package org.example.gui.utils.dashboard.recommendations;

import org.example.database.CustomerDAO;
import org.example.database.DBConnect;
import org.example.database.OrderDAO;
import org.example.gui.Mainframe;
import org.example.gui.utils.creators.roundedPanel;
import org.example.gui.utils.creators.roundedBorder;
import org.example.gui.utils.fonts.fontManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.util.Vector;

public class recentOrders extends roundedPanel {
    private roundedPanel headingPanel;
    private JTable table;
    private DefaultTableModel tableModel;
    private final Mainframe frame;
    private OrderDAO orderDAO;
    private CustomerDAO customerDAO;

    public recentOrders(Mainframe frame) {
        super(16);
        this.frame = frame;

        initializeDAOs();
        initComponents();
    }

    /**
     * Initializes the Data Access Objects (DAOs) for this panel.
     */
    private void initializeDAOs() {
        try {
            Connection conn = DBConnect.getConnection();
            if (conn != null && !conn.isClosed()) {
                this.orderDAO = new OrderDAO(conn);
                this.customerDAO = new CustomerDAO(conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIManager.getColor("Menu.background"));
        setBorder(new roundedBorder(16, UIManager.getColor("listBorder"), 2));
        setOpaque(false);

        // === Heading Panel ===
        headingPanel = new roundedPanel(16);
        headingPanel.setLayout(new BorderLayout());
        headingPanel.setBackgroundColorKey("Menu.background");
        headingPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel headingLabel = new JLabel("Recent Orders");
        headingLabel.setHorizontalAlignment(SwingConstants.LEFT);
        headingLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
        fontManager.applyHeading(headingLabel, 4);

        headingPanel.add(headingLabel, BorderLayout.CENTER);
        add(headingPanel);
        add(Box.createVerticalStrut(10));

        // === Table ---
        // Define columns
        String[] columns = {"Order ID", "Laundromat", "Status"};

        // Create an empty model
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };

        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setOpaque(true);
        table.setBackground(UIManager.getColor("Panel.background"));
        table.setForeground(UIManager.getColor("Label.foreground"));

        // Set table header colors
        table.getTableHeader().setBackground(UIManager.getColor("background"));
        table.getTableHeader().setForeground(UIManager.getColor("Label.foreground"));
        table.getTableHeader().setFont(fontManager.h7());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(UIManager.getColor("Panel.background"));
        tableWrapper.add(scrollPane, BorderLayout.CENTER);
        tableWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        add(tableWrapper);
    }

    /**
     * Fetches the 3 most recent orders for the current user and
     * populates the table.
     */
    public void loadRecentOrders() {
        if (orderDAO == null || customerDAO == null) {
            System.err.println("recentOrders: DAOs not initialized. Cannot load orders.");
            return;
        }

        // 1. Get current username from Mainframe
        String username = frame.getCurrentUser();
        if (username == null) {
            tableModel.setRowCount(0); // Clear table if no user is logged in
            return;
        }


        try {
            int custID = customerDAO.getCustomerId(username);
            if (custID == -1) {
                System.err.println("recentOrders: Could not find customer ID for: " + username);
                tableModel.setRowCount(0);
                return;
            }

            // 3. Fetch recent orders from OrderDAO
            Vector<Vector<Object>> data = orderDAO.getRecentOrders(custID);

            tableModel.setRowCount(0);

            if (data.isEmpty()) {
                // Optionally show a message
                tableModel.addRow(new Object[]{"-", "No recent orders found", "-"});
            } else {
                for (Vector<Object> row : data) {
                    tableModel.addRow(row);
                }
            }

            if (table != null) {
                table.revalidate();
                table.repaint();
                Component parent = table.getParent();
                if (parent instanceof JViewport) {
                    parent.revalidate();
                    parent.repaint();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            tableModel.setRowCount(0);
            tableModel.addRow(new Object[]{"-", "Error loading orders", "-"});
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();
        // Re-apply colors on theme change
        setBackground(UIManager.getColor("Menu.background"));
        setBorder(new roundedBorder(16, UIManager.getColor("listBorder"), 2));

        if (headingPanel != null) {
            headingPanel.setBackgroundColorKey("Menu.background");
            headingPanel.revalidate();
            headingPanel.repaint();
        }
        if (table != null) {
            table.setBackground(UIManager.getColor("Panel.background"));
            table.setForeground(UIManager.getColor("Label.foreground"));
            table.getTableHeader().setBackground(UIManager.getColor("background"));
            table.getTableHeader().setForeground(UIManager.getColor("Label.foreground"));
        }
    }
}