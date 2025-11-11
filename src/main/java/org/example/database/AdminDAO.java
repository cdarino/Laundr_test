package org.example.database;

import java.sql.*;
import java.util.Vector;
import org.example.database.NotificationDAO;
import java.util.List;
/**
 * DAO for Admin operations.
 * Manages Admin Login, Customer/Order viewing, and Laundromat C.R.U.D.
 *
 */
public class AdminDAO {

    private Connection connection;

    public AdminDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Validates admin login credentials.
     */
    public boolean validateLogin(String username, String password) {
        if (connection == null) {
            System.err.println("Cannot validate login — no database connection.");
            return false;
        }

        String query = "SELECT * FROM admin WHERE adminUsername = '" + username + "' AND adminPassword = '" + password + "'";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fetches all customers.
     */
    public Vector<Vector<Object>> getAllCustomers() throws SQLException {
        if (connection == null) {
            throw new SQLException("Cannot fetch customers — no database connection.");
        }
        String query = "SELECT custID, custUsername, custPhone, custAddress, custEmail FROM customer";
        Vector<Vector<Object>> data = new Vector<>();

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("custID"));
                row.add(rs.getString("custUsername"));
                row.add(rs.getString("custPhone"));
                row.add(rs.getString("custAddress"));
                row.add(rs.getString("custEmail"));
                data.add(row);
            }
        }
        return data;
    }

    /**
     * Fetches all laundromats. Used by AdminView.java.
     */
    public Vector<Vector<Object>> getAllLaundromats() throws SQLException {
        if (connection == null) {
            throw new SQLException("Cannot fetch laundromats — no database connection.");
        }
        String query = "SELECT laundromatID, laundromatName, laundromatAddress, rating, " +
        "imagePath, distanceFromUser, estimatedTime, pricePerLoad, highlights FROM laundromat";

        Vector<Vector<Object>> data = new Vector<>();

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("laundromatID"));
                row.add(rs.getString("laundromatName"));
                row.add(rs.getString("laundromatAddress"));
                row.add(rs.getBigDecimal("rating"));
                row.add(rs.getString("imagePath"));
                row.add(rs.getString("distanceFromUser"));
                row.add(rs.getString("estimatedTime"));
                row.add(rs.getBigDecimal("pricePerLoad")); // or rs.getDouble("pricePerLoad")
                row.add(rs.getString("highlights"));
                data.add(row);
            }
        }
        return data;
    }

    /**
     * Fetches orders for a specific customer ID.
     */
    public Vector<Vector<Object>> getOrdersByCustomerId(int custId) throws SQLException {
        if (connection == null) {
            throw new SQLException("Cannot fetch orders — no database connection.");
        }

        String query = "SELECT orderID, laundromatID, orderDate, orderStatus, totalAmount " +
                "FROM orders WHERE custID = " + custId + " ORDER BY orderDate DESC";

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
     * Adds a new laundromat to the database. Used by AdminView.java.
     */
    public boolean addLaundromat(String name, String address, String imagePath, String distance, String estTime, String highlights, double pricePerLoad) throws SQLException {
    if (connection == null) {
        throw new SQLException("Cannot add laundromat — no database connection.");
    }

    // Simple sanitation for string inputs
    name = name == null || name.isEmpty() ? "NULL" : "'" + name.replace("'", "''") + "'";
    address = address == null || address.isEmpty() ? "NULL" : "'" + address.replace("'", "''") + "'";
    imagePath = imagePath == null || imagePath.isEmpty() ? "'Pictures/default.png'" : "'" + imagePath.replace("'", "''") + "'";
    distance = distance == null || distance.isEmpty() ? "'N/A'" : "'" + distance.replace("'", "''") + "'";
    estTime = estTime == null || estTime.isEmpty() ? "'N/A'" : "'" + estTime.replace("'", "''") + "'";
    highlights = highlights == null || highlights.isEmpty() ? "NULL" : "'" + highlights.replace("'", "''") + "'";
    String priceExpr = String.format("%.2f", pricePerLoad);

    String query = "INSERT INTO laundromat (laundromatName, laundromatAddress, imagePath, distanceFromUser, estimatedTime, highlights, pricePerLoad) VALUES ("
            + name + ", "
            + address + ", "
            + imagePath + ", "
            + distance + ", "
            + estTime + ", "
            + highlights + ", "
            + priceExpr + ")";

    try (Statement st = connection.createStatement()) {
        int rowsAffected = st.executeUpdate(query);
        return rowsAffected > 0;
    }
}

    /**
     * Updates an existing laundromat in the database.
     */
    public boolean updateLaundromat(int laundromatID, String name, String address, String imagePath, String distance, String estTime, String highlights, double pricePerLoad) throws SQLException {
    if (connection == null) {
        throw new SQLException("Cannot update laundromat — no database connection.");
    }

    // Simple sanitation for string inputs
    name = name == null || name.isEmpty() ? "NULL" : "'" + name.replace("'", "''") + "'";
    address = address == null || address.isEmpty() ? "NULL" : "'" + address.replace("'", "''") + "'";
    imagePath = imagePath == null || imagePath.isEmpty() ? "'Pictures/default.png'" : "'" + imagePath.replace("'", "''") + "'";
    distance = distance == null || distance.isEmpty() ? "'N/A'" : "'" + distance.replace("'", "''") + "'";
    estTime = estTime == null || estTime.isEmpty() ? "'N/A'" : "'" + estTime.replace("'", "''") + "'";
    highlights = highlights == null || highlights.isEmpty() ? "NULL" : "'" + highlights.replace("'", "''") + "'";
    String priceExpr = String.format("%.2f", pricePerLoad);

    String query = "UPDATE laundromat SET " +
            "laundromatName = " + name + ", " +
            "laundromatAddress = " + address + ", " +
            "imagePath = " + imagePath + ", " +
            "distanceFromUser = " + distance + ", " +
            "estimatedTime = " + estTime + ", " +
            "highlights = " + highlights + ", " +
            "pricePerLoad = " + priceExpr + " " +
            "WHERE laundromatID = " + laundromatID;

    try (Statement st = connection.createStatement()) {
        int rowsAffected = st.executeUpdate(query);
        return rowsAffected > 0;
    }
}
    // find the custid for a given orderid.
    //     * needed by updateorderstatus to create a notification.
    private int getCustIDForOrder(int orderID) throws SQLException {
        String findCustQuery = "SELECT custID FROM orders WHERE orderID = " + orderID;

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(findCustQuery)) {
            if (rs.next()) {
                return rs.getInt("custID");
            }
        }
        return -1; // order not found
    }

    /**
     * updates the status of a specific order.
     * after updating, it creates a notification.
     */
    public boolean updateOrderStatus(int orderID, String newStatus) throws SQLException {
        if (connection == null) {
            throw new SQLException("cannot update status — no database connection.");
        }

        // a customerid is needed for the notification. let's find it.
        int custID = getCustIDForOrder(orderID);

        if (custID == -1) {
            throw new SQLException("cannot update status — orderid " + orderID + " not found.");
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
        }
        return false;
    }
}