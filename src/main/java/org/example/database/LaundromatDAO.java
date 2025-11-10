package org.example.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

/**
 * Data Access Object for Laundromat-related queries.
 * This class uses String concatenation for SQL queries.
 */
public class LaundromatDAO {

    private Connection connection;

    public LaundromatDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Gets a simple list of all laundromat names.
     */
    public Vector<String> getAllLaundromatNames() {
        Vector<String> laundromatNames = new Vector<>();
        if (connection == null) {
            System.err.println("Cannot get laundromats — no database connection.");
            return laundromatNames;
        }

        String query = "SELECT laundromatName FROM laundromat ORDER BY laundromatName";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                laundromatNames.add(rs.getString("laundromatName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return laundromatNames;
    }
}