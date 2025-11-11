package org.example.database;

import org.example.service.Service;
import org.example.service.DynamicService; // see below
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    private Connection conn;

    public ServiceDAO(Connection conn) {
        this.conn = conn;
    }

    public List<Service> getAllServices() throws SQLException {
        List<Service> services = new ArrayList<>();

        String sql = "SELECT laundromatID, serviceName, description, basePrice, estimatedTime, iconPath FROM service";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name = rs.getString("serviceName");
                String desc = rs.getString("description");
                double price = rs.getDouble("basePrice");
                String icon = rs.getString("iconPath");

                // if icon is null, fallback to default
                if (icon == null || icon.isBlank()) {
                    icon = defaultIconFor(name);
                }

                services.add(new DynamicService(name, desc, price, icon));
            }
        }

        return services;
    }

    public int getServiceIdForName(int laundromatId, String serviceName) {
    if (conn == null) return -1;
    // If laundromatId is <= 0, try to match by name only (fallback)
    if (laundromatId > 0) {
        String sql = "SELECT serviceID FROM service WHERE laundromatID = ? AND serviceName = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, laundromatId);
            ps.setString(2, serviceName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("serviceID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // fallback: match by name only
    String sql2 = "SELECT serviceID FROM service WHERE serviceName = ? LIMIT 1";
    try (PreparedStatement ps = conn.prepareStatement(sql2)) {
        ps.setString(1, serviceName);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("serviceID");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return -1;
}

/**
 * Lookup unit price for a serviceID. Returns -1 on error/not found.
 */
public double getPriceForServiceId(int serviceId) {
    if (conn == null) return -1.0;
    String sql = "SELECT basePrice FROM service WHERE serviceID = ? LIMIT 1";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, serviceId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble("basePrice");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return -1.0;
}

    private String defaultIconFor(String serviceName) {
        return switch (serviceName) {
            case "Dry Clean" -> "Icons/Services/dryClean.svg";
            case "Wash & Fold" -> "Icons/Services/washFold.svg";
            case "Press Only" -> "Icons/Services/pressOnly.svg";
            case "Bulky Items" -> "Icons/Services/bulkyItems.svg";
            case "Sneakers" -> "Icons/Services/sneakers.svg";
            case "Pet Items" -> "Icons/Services/petItems.svg";
            default -> "Icons/Services/default.svg";
        };
    }
}
