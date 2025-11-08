package org.example.database;

import org.example.models.Customer;
import java.sql.*;

/**
 * UPDATED with console print statements for debugging.
 */
public class CustomerDAO {
    private final Connection connection;

    public CustomerDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean registerCustomer(Customer customer) {
        if (connection == null) {
            System.err.println("Cannot register customer — no database connection.");
            return false;
        }

        String query = "INSERT INTO customer (custUsername, custPassword, custPhone, custAddress, custEmail) VALUES ('"
                + customer.getUsername() + "', '"
                + customer.getPassword() + "', '"
                + customer.getPhone() + "', '"
                + customer.getAddress() + "', '"
                + customer.getEmail() + "')";

        try (Statement st = connection.createStatement()) {

            st.executeUpdate(query);
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("⚠ Username already exists.");
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Validates customer login using a PreparedStatement to prevent SQL injection.
     */
    public boolean validateLogin(String username, String password) {
        if (connection == null) {
            System.err.println("Cannot validate login — no database connection.");
            return false;
        }

        // Use string concatenation
        String query = "SELECT * FROM customer WHERE custUsername = '" + username + "' AND custPassword = '" + password + "'";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            // rs.next() will be true if a matching record was found
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- NEW METHOD ---
    /**
     * Gets a customer's ID from their username.
     * @param username The username to search for.
     * @return The integer custID, or -1 if not found.
     */
    public int getCustomerId(String username) {
        if (connection == null) {
            System.err.println("Cannot get customer ID — no database connection.");
            return -1;
        }

        // --- DEBUG PRINT ---
        System.out.println("[CustomerDAO.getCustomerId] Searching for user: " + username);
        // ---

        String query = "SELECT custID FROM customer WHERE custUsername = '" + username + "'";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            if (rs.next()) {
                int id = rs.getInt("custID");
                // --- DEBUG PRINT ---
                System.out.println("   -> Found ID: " + id);
                // ---
                return id;
            } else {
                // --- DEBUG PRINT ---
                System.out.println("   -> User not found.");
                // ---
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Not found
    }

    // --- EXISTING METHODS (for profile page) ---

    /**
     * Gets a single customer's data by their username.
     * @param username The username to search for.
     * @return A Customer object, or null if not found.
     */
    public Customer getCustomerByUsername(String username) {
        if (connection == null) {
            System.err.println("Cannot get customer — no database connection.");
            return null;
        }

        String query = "SELECT * FROM customer WHERE custUsername = '" + username + "'";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            if (rs.next()) {
                // Create a Customer object from the result set
                Customer customer = new Customer(
                        rs.getString("custUsername"),
                        null, // Do not retrieve or store the password
                        rs.getString("custPhone"),
                        rs.getString("custAddress"),
                        rs.getString("custEmail")
                );
                customer.setCustID(rs.getInt("custID")); // Set the ID as well
                return customer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Not found
    }

    // --- NEW METHOD ---
    /**
     * Checks if a username already exists in the database.
     * @param username The username to check.
     * @return true if the username exists, false otherwise.
     */
    public boolean usernameExists(String username) {
        if (connection == null) {
            System.err.println("Cannot check username — no database connection.");
            return false;
        }

        String query = "SELECT custID FROM customer WHERE custUsername = '" + username + "'";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            return rs.next(); // true if a record is found
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- MODIFIED METHOD ---
    /**
     * Updates a customer's profile information using their ID.
     * @param custId The ID of the customer to update.
     * @param newUsername The new username.
     * @param newEmail The new email.
     * @param newPhone The new phone number.
     * @return true if successful, false otherwise.
     */
    public boolean updateProfile(int custId, String newUsername, String newEmail, String newPhone) {
        if (connection == null) {
            System.err.println("Cannot update profile — no database connection.");
            return false;
        }

        String query = "UPDATE customer SET " +
                "custUsername = '" + newUsername + "', " +
                "custEmail = '" + newEmail + "', " +
                "custPhone = '" + newPhone + "' " +
                "WHERE custID = " + custId; // Use ID to find the user

        try (Statement st = connection.createStatement()) {
            int rowsAffected = st.executeUpdate(query);
            return rowsAffected > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            // This will catch a duplicate username if the check in profileCard fails
            System.err.println("⚠ SQL Integrity Error (e.g., duplicate username): " + e.getMessage());
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a customer's address.
     */
    public boolean updateAddress(String username, String newAddress) {
        if (connection == null) {
            System.err.println("Cannot update address — no database connection.");
            return false;
        }

        String query = "UPDATE customer SET " +
                "custAddress = '" + newAddress + "' " +
                "WHERE custUsername = '" + username + "'";

        try (Statement st = connection.createStatement()) {
            int rowsAffected = st.executeUpdate(query);
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}