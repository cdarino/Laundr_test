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
}
