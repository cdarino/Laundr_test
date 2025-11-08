package org.example.database;

import org.example.models.Customer;
import java.sql.*;

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

    public boolean validateLogin(String username, String password) {
        if (connection == null) {
            System.err.println("Cannot validate login — no database connection.");
            return false;
        }

        String query = "SELECT * FROM customer WHERE custUsername = '" + username + "' AND custPassword = '" + password + "'";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getCustomerId(String username) {
        if (connection == null) {
            System.err.println("Cannot get customer ID — no database connection.");
            return -1;
        }

        String query = "SELECT custID FROM customer WHERE custUsername = '" + username + "'";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            if (rs.next()) {
                int id = rs.getInt("custID");

                return id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Customer getCustomerByUsername(String username) {
        if (connection == null) {
            System.err.println("Cannot get customer — no database connection.");
            return null;
        }

        String query = "SELECT * FROM customer WHERE custUsername = '" + username + "'";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            if (rs.next()) {
                // create a Customer object from the result set
                Customer customer = new Customer(
                        rs.getString("custUsername"),
                        null, // do not retrieve or store the password
                        rs.getString("custPhone"),
                        rs.getString("custAddress"),
                        rs.getString("custEmail")
                );
                customer.setCustID(rs.getInt("custID")); // set the ID as well
                return customer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean usernameExists(String username) {
        if (connection == null) {
            System.err.println("Cannot check username — no database connection.");
            return false;
        }

        String query = "SELECT custID FROM customer WHERE custUsername = '" + username + "'";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProfile(int custId, String newUsername, String newEmail, String newPhone) {
        if (connection == null) {
            System.err.println("Cannot update profile — no database connection.");
            return false;
        }

        String query = "UPDATE customer SET " +
                "custUsername = '" + newUsername + "', " +
                "custEmail = '" + newEmail + "', " +
                "custPhone = '" + newPhone + "' " +
                "WHERE custID = " + custId; // use ID to find the user

        try (Statement st = connection.createStatement()) {
            int rowsAffected = st.executeUpdate(query);
            return rowsAffected > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("⚠ SQL Integrity Error (e.g., duplicate username): " + e.getMessage());
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

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