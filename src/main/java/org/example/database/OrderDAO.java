package org.example.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

/**
 * Data Access Object for Order-related queries.
 *
 * all queries use string concatenation as requested.
 *
 * updated:
 * - added getallcompletedorders for the torate panel.
 */
public class OrderDAO {

    private Connection connection;

    public OrderDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Fetches the 3 most recent orders for the dashboard's recentOrders panel.
     *
     * @param custID The ID of the customer.
     * @return A Vector of Vectors, where each inner Vector is a row: [OrderID, LaundromatName, Status]
     */
    public Vector<Vector<Object>> getRecentOrders(int custID) {
        Vector<Vector<Object>> data = new Vector<>();
        if (connection == null) {
            System.err.println("[OrderDAO.getRecentOrders] Cannot get orders — no database connection.");
            return data;
        }

        // this query joins orders and laundromat to get the laundromat's name
        // and orders by date descending, limiting to 3.
        String query = "SELECT o.orderID, l.laundromatName, o.orderStatus " +
                "FROM orders o " +
                "JOIN laundromat l ON o.laundromatID = l.laundromatID " +
                "WHERE o.custID = " + custID + " " +
                "ORDER BY o.orderDate DESC " +
                "LIMIT 3";

        // --- debug print ---
        System.out.println("[OrderDAO.getRecentOrders] Executing query: " + query);
        // ---

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
                // --- debug print ---
                System.out.println("   -> Found row: " + row);
                // ---
            }
            // --- debug print ---
            System.out.println("[OrderDAO.getRecentOrders] Total rows found: " + rowsFound);
            // ---
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * A dynamic method to fetch orders based on a list of statuses.
     * Used by both the "Orders" panel and the "To Receive" panel.
     *
     * @param custID    The customer's ID.
     * @param statuses  A List of statuses to include (e.g., ["pending", "in_progress"]).
     * @param sortOrder The SQL sort order ("ASC" for ascending, "DESC" for descending).
     * @return A Vector of Vectors, where each inner Vector is a row: [OrderID, LaundromatName, Address, TotalAmount, OrderDate]
     */
    public Vector<Vector<Object>> getDynamicOrders(int custID, List<String> statuses, String sortOrder) {
        Vector<Vector<Object>> data = new Vector<>();
        if (connection == null || statuses == null || statuses.isEmpty()) {
            return data;
        }

        // 1. build the "IN (...)" part of the query
        // e.g., "'pending', 'accepted', 'in_progress'"
        StringBuilder statusListString = new StringBuilder();
        for (int i = 0; i < statuses.size(); i++) {
            statusListString.append("'").append(statuses.get(i)).append("'");
            if (i < statuses.size() - 1) {
                statusListString.append(", ");
            }
        }

        // 2. build the full query
        String query = "SELECT o.orderID, l.laundromatName, l.laundromatAddress, o.totalAmount, o.orderDate " +
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
                // format the datetime as a string
                row.add(rs.getTimestamp("orderDate").toString());
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
     *
     * @param custID The customer's ID.
     * @return A Vector of Vectors, where each inner Vector is a row: [OrderID, LaundromatID, LaundromatName]
     */
    public Vector<Vector<Object>> getAllCompletedOrders(int custID) {
        Vector<Vector<Object>> data = new Vector<>();
        if (connection == null) {
            return data;
        }

        // this query finds all of a customer's 'completed' orders
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
}