package org.example.database;

import java.sql.*;
import java.util.Vector;

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
                "imagePath, distanceFromUser, estimatedTime, highlights FROM laundromat";

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
    public boolean addLaundromat(String name, String address, String imagePath, String distance, String estTime, String highlights) throws SQLException {
        if (connection == null) {
            throw new SQLException("Cannot add laundromat — no database connection.");
        }

        // Simple sanitation
        name = name.isEmpty() ? "NULL" : "'" + name.replace("'", "''") + "'";
        address = address.isEmpty() ? "NULL" : "'" + address.replace("'", "''") + "'";
        imagePath = imagePath.isEmpty() ? "'Pictures/default.png'" : "'" + imagePath.replace("'", "''") + "'";
        distance = distance.isEmpty() ? "'N/A'" : "'" + distance.replace("'", "''") + "'";
        estTime = estTime.isEmpty() ? "'N/A'" : "'" + estTime.replace("'", "''") + "'";
        highlights = highlights.isEmpty() ? "NULL" : "'" + highlights.replace("'", "''") + "'";

        String query = "INSERT INTO laundromat (laundromatName, laundromatAddress, imagePath, distanceFromUser, estimatedTime, highlights) VALUES ("
                + name + ", "
                + address + ", "
                + imagePath + ", "
                + distance + ", "
                + estTime + ", "
                + highlights + ")";

        try (Statement st = connection.createStatement()) {
            int rowsAffected = st.executeUpdate(query);
            return rowsAffected > 0;
        }
    }

    /**
     * Updates an existing laundromat in the database.
     */
    public boolean updateLaundromat(int laundromatID, String name, String address, String imagePath, String distance, String estTime, String highlights) throws SQLException {
        if (connection == null) {
            throw new SQLException("Cannot update laundromat — no database connection.");
        }

        // Simple sanitation
        name = name.isEmpty() ? "NULL" : "'" + name.replace("'", "''") + "'";
        address = address.isEmpty() ? "NULL" : "'" + address.replace("'", "''") + "'";
        imagePath = imagePath.isEmpty() ? "'Pictures/default.png'" : "'" + imagePath.replace("'", "''") + "'";
        distance = distance.isEmpty() ? "'N/A'" : "'" + distance.replace("'", "''") + "'";
        estTime = estTime.isEmpty() ? "'N/A'" : "'" + estTime.replace("'", "''") + "'";
        highlights = highlights.isEmpty() ? "NULL" : "'" + highlights.replace("'", "''") + "'";

        // UPDATED: Query now updates the new UI-focused columns
        String query = "UPDATE laundromat SET " +
                "laundromatName = " + name + ", " +
                "laundromatAddress = " + address + ", " +
                "imagePath = " + imagePath + ", " +
                "distanceFromUser = " + distance + ", " +
                "estimatedTime = " + estTime + ", " +
                "highlights = " + highlights + " " +
                "WHERE laundromatID = " + laundromatID;

        try (Statement st = connection.createStatement()) {
            int rowsAffected = st.executeUpdate(query);
            return rowsAffected > 0;
        }
    }
}