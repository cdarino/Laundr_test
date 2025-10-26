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

        String query = "INSERT INTO customer (custUsername, custPassword, custPhone, custAddress, custEmail) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, customer.getUsername());
            ps.setString(2, customer.getPassword());
            ps.setString(3, customer.getPhone());
            ps.setString(4, customer.getAddress());
            ps.setString(5, customer.getEmail());
            ps.executeUpdate();
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

        String query = "SELECT * FROM customer WHERE custUsername = ? AND custPassword = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
