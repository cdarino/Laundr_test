package org.example.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

/**
 * Data Access Object for Order-related queries.
 */
public class OrderDAO {

    private Connection connection;

    public OrderDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * ftches the 3 most recent orders for recentOrders panel.
     */
    public Vector<Vector<Object>> getRecentOrders(int custID) {
        Vector<Vector<Object>> data = new Vector<>();
        if (connection == null) {
            System.err.println("[OrderDAO.getRecentOrders] Cannot get orders — no database connection.");
            return data;
        }

        // joins orders and laundromat to get the laundromat's name
        // and orders by date descending, limiting to 3.
        String query = "SELECT o.orderID, l.laundromatName, o.orderStatus " +
                "FROM orders o " +
                "JOIN laundromat l ON o.laundromatID = l.laundromatID " +
                "WHERE o.custID = " + custID + " " +
                "ORDER BY o.orderDate DESC " +
                "LIMIT 3";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            int rowsFound = 0;
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("orderID"));
                row.add(rs.getString("laundromatName"));
                row.add(rs.getString("orderStatus"));
                data.add(row);
                rowsFound++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /*
     * fetch orders based on a list of statuses
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
                "AND o.orderStatus IN (" + statusListString.toString() + ") " +
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
     * Fetches *all* completed orders for a user.
     * Used by the "ToRate" panel.
     */
    public Vector<Vector<Object>> getAllCompletedOrders(int custID) {
        Vector<Vector<Object>> data = new Vector<>();
        if (connection == null) {
            return data;
        }

        // finds all of a customer's 'completed' orders
        String query = "SELECT o.orderID, o.laundromatID, l.laundromatName " +
                "FROM orders o " +
                "JOIN laundromat l ON o.laundromatID = l.laundromatID " +
                "WHERE o.custID = " + custID + " " +
                "AND o.orderStatus = 'completed' " +
                "ORDER BY o.orderDate DESC";

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

    /**
     * Updates the status of a specific order.
     * This is the "Subject's" action in the Observer pattern.
     * After updating, it creates a notification.
     */
    public boolean updateOrderStatus(int orderID, String newStatus) {
        if (connection == null) {
            System.err.println("Cannot update status — no database connection.");
            return false;
        }

        // a customerid is needed for the notification. let's find it.
        int custID = -1;
        String findCustQuery = "SELECT custID FROM orders WHERE orderID = " + orderID;

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(findCustQuery)) {
            if (rs.next()) {
                custID = rs.getInt("custID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // can't find customer, so don't update
        }

        if (custID == -1) {
            System.err.println("Cannot update status — orderID not found.");
            return false;
        }

        // 1. update the order status
        String updateQuery = "UPDATE orders SET orderStatus = '" + newStatus + "' WHERE orderID = " + orderID;
        try (Statement st = connection.createStatement()) {
            int rowsAffected = st.executeUpdate(updateQuery);
            if (rowsAffected > 0) {
                // 2. notify the observer (by creating a notification)
                String message = "Your order #" + orderID + " is now: " + newStatus;
                NotificationDAO notificationDAO = new NotificationDAO(connection);
                notificationDAO.createNotification(custID, message);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}