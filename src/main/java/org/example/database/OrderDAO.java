package org.example.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

/**
 * Data Access Object for Order-related queries.
 * UPDATED with console print statements for debugging.
 */
public class OrderDAO {

    private Connection connection;

    public OrderDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Gets the 3 most recent orders for a specific customer.
     * Joins with the laundromat table to get the name.
     * (Used by Dashboard)
     */
    public Vector<Vector<Object>> getRecentOrders(int custID) {
        Vector<Vector<Object>> data = new Vector<>();
        if (connection == null) {
            System.err.println("[OrderDAO.getRecentOrders] Cannot get orders — no database connection.");
            return data;
        }

        String query = "SELECT o.orderID, l.laundromatName, o.orderStatus " +
                "FROM orders o " +
                "JOIN laundromat l ON o.laundromatID = l.laundromatID " +
                "WHERE o.custID = " + custID + " " +
                "ORDER BY o.orderDate DESC " +
                "LIMIT 3";

        // --- DEBUG PRINT ---
        System.out.println("[OrderDAO.getRecentOrders] Executing query: " + query);
        // ---

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("orderID"));
                row.add(rs.getString("laundromatName"));
                row.add(rs.getString("orderStatus"));

                // --- DEBUG PRINT ---
                System.out.println("   -> Found row: " + row);
                // ---

                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // --- DEBUG PRINT ---
        System.out.println("[OrderDAO.getRecentOrders] Total rows found: " + data.size());
        // ---
        return data;
    }

    /**
     * Gets all orders for a specific customer ID.
     * (Used by the "My Orders" panel)
     */
    public Vector<Vector<Object>> getOrdersByCustomerID(int custID) throws SQLException {
        if (connection == null) {
            throw new SQLException("Cannot fetch orders — no database connection.");
        }

        String query = "SELECT orderID, laundromatID, orderDate, orderStatus, totalAmount " +
                "FROM orders WHERE custID = " + custID + " ORDER BY orderDate DESC";

        Vector<Vector<Object>> data = new Vector<>();

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("orderID"));
                row.add(rs.getInt("laundromatID"));
                row.add(rs.getTimestamp("orderDate").toString());
                row.add(rs.getString("orderStatus"));
                row.add(rs.getBigDecimal("totalAmount"));
                data.add(row);
            }
        }
        return data;
    }

    /**
     * Gets all completed orders for a customer that have not yet been reviewed.
     * (Used by the "ToRate" panel)
     */
    public Vector<Vector<Object>> getUnreviewedCompletedOrders(int custID) throws SQLException {
        if (connection == null) {
            throw new SQLException("Cannot fetch orders — no database connection.");
        }

        String query = "SELECT o.orderID, l.laundromatName, o.laundromatID " +
                "FROM orders o " +
                "JOIN laundromat l ON o.laundromatID = l.laundromatID " +
                "WHERE o.custID = " + custID + " AND o.orderStatus = 'completed' " +
                "AND NOT EXISTS (" +
                "    SELECT 1 FROM review r WHERE r.orderID = o.orderID" +
                ")";

        Vector<Vector<Object>> data = new Vector<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("orderID"));
                row.add(rs.getString("laundromatName"));
                row.add(rs.getInt("laundromatID"));
                data.add(row);
            }
        }
        return data;
    }
}