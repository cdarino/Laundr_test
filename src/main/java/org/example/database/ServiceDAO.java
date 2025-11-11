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
