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
                        rs.getString("custEmail"),
                        rs.getDouble("walletBalance")
                );
                customer.setCustID(rs.getInt("custID")); // set the ID as well
                customer.setWalletBalance(rs.getDouble("walletBalance"));
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

     //fetches only the wallet balance for a given customer id.
    public double getWalletBalance(int custID) throws SQLException {
        if (connection == null) {
            throw new SQLException("no database connection.");
        }
        String query = "SELECT walletBalance FROM customer WHERE custID = " + custID;
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                return rs.getDouble("walletBalance");
            }
        }
        // if user not found or error, return 0
        return 0.0;
    }

    //adds a specified amount to a user's current wallet balance.
    public double addWalletBalance(int custID, double amountToAdd) {
        if (connection == null) {
            System.err.println("digitalwallet: no connection.");
            return -1.0;
        }

        if (amountToAdd <= 0) {
            System.err.println("digitalwallet: amount to add must be positive.");
            return -1.0;
        }

        try {
            // 1. get current balance
            double currentBalance = 0.00;
            String getQuery = "SELECT walletBalance FROM customer WHERE custID = " + custID;
            try (Statement st = connection.createStatement();
                 ResultSet rs = st.executeQuery(getQuery)) {
                if (rs.next()) {
                    currentBalance = rs.getDouble("walletBalance");
                }
            }

            // 2. calculate new balance
            double newBalance = currentBalance + amountToAdd;

            // 3. update the database
            String updateQuery = "UPDATE customer SET walletBalance = " + newBalance + " WHERE custID = " + custID;
            try (Statement st = connection.createStatement()) {
                int rowsAffected = st.executeUpdate(updateQuery);
                if (rowsAffected > 0) {
                    return newBalance; // return the new balance on success
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1.0; // indicate failure
    }
}