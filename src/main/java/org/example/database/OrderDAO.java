package org.example.database;

import java.sql.*;
import java.util.List;
import java.util.Vector;

/**
 * Data Access Object for Order-related queries.
 */
public class OrderDAO {

    private final Connection connection;

    public OrderDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Recent orders helper (used elsewhere).
     */
    public Vector<Vector<Object>> getRecentOrders(int custID) {
        Vector<Vector<Object>> data = new Vector<>();
        if (connection == null) return data;

        String query =
                "SELECT o.orderID, l.laundromatName, o.orderStatus " +
                "FROM orders o " +
                "JOIN laundromat l ON o.laundromatID = l.laundromatID " +
                "WHERE o.custID = " + custID + " " +
                "ORDER BY o.orderDate DESC LIMIT 3";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("orderID"));
                row.add(rs.getString("laundromatName"));
                row.add(rs.getString("orderStatus"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /*
     * Fetch orders based on a list of statuses
     * used by both the "Orders" panel and the "To Receive" panel
     */
    public Vector<Vector<Object>> getDynamicOrders(int custID, List<String> statuses, String sortOrder) {
        Vector<Vector<Object>> data = new Vector<>();
        if (connection == null || statuses == null || statuses.isEmpty()) {
            return data;
        }

        StringBuilder statusListString = new StringBuilder();
        for (int i = 0; i < statuses.size(); i++) {
            statusListString.append("'").append(statuses.get(i)).append("'");
            if (i < statuses.size() - 1) {
                statusListString.append(", ");
            }
        }

        String query = "SELECT o.orderID, l.laundromatName, l.laundromatAddress, o.totalAmount, o.orderDate, o.orderStatus " +
                "FROM orders o " +
                "JOIN laundromat l ON o.laundromatID = l.laundromatID " +
                "WHERE o.custID = " + custID + " " +
                "AND o.orderStatus IN (" + statusListString + ") " +
                "ORDER BY o.orderDate " + sortOrder;

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("orderID"));
                row.add(rs.getString("laundromatName"));
                row.add(rs.getString("laundromatAddress"));
                row.add(rs.getBigDecimal("totalAmount"));
                row.add(rs.getTimestamp("orderDate").toString());
                row.add(rs.getString("orderStatus"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Fetches all completed orders for a user.
     */
    public Vector<Vector<Object>> getAllCompletedOrders(int custID) {
        Vector<Vector<Object>> data = new Vector<>();
        if (connection == null) return data;

        String query =
                "SELECT o.orderID, o.laundromatID, l.laundromatName " +
                "FROM orders o " +
                "JOIN laundromat l ON o.laundromatID = l.laundromatID " +
                "WHERE o.custID = " + custID + " AND o.orderStatus = 'completed'";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("orderID"));
                row.add(rs.getInt("laundromatID"));
                row.add(rs.getString("laundromatName"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // ================== NEW: ORDER CREATION ==================

    /**
     * Inserts a new order and returns the generated orderID.
     * Assumes table has:
     *   custID, laundromatID, orderDate (DEFAULT CURRENT_TIMESTAMP),
     *   orderStatus, totalAmount, instructions, paymentMethod
     *
     * If your schema differs, adjust the SQL accordingly.
     *
     * @return generated orderID or -1 if failed.
     */
    public int createOrder(int custID,
                           int laundromatID,
                           double totalAmount,
                           String instructions,
                           String paymentMethod) {
        if (connection == null) {
            System.err.println("createOrder: No database connection.");
            return -1;
        }

        String sql = "INSERT INTO orders " +
                     "(custID, laundromatID, orderDate, orderStatus, totalAmount, instructions, paymentMethod) " +
                     "VALUES (?, ?, CURRENT_TIMESTAMP, 'pending', ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, custID);
            ps.setInt(2, laundromatID);
            ps.setDouble(3, totalAmount);
            ps.setString(4, instructions != null ? instructions : "");
            ps.setString(5, paymentMethod != null ? paymentMethod : "unknown");

            int affected = ps.executeUpdate();
            if (affected == 0) {
                System.err.println("createOrder: Insert affected 0 rows.");
                return -1;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}