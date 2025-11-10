package org.example.database;

import org.example.models.ReviewData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Data Access Object for Review-related queries.
 */
public class ReviewDAO {

    private Connection connection;

    public ReviewDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserts a new review and then updates the laundromat's average rating.
     *
     */
    public boolean addReviewAndUpdateAverage(int orderID, int laundromatID, int custID, int rating, String comment) {
        if (connection == null) {
            System.err.println("cannot add review — no database connection.");
            return false;
        }

        // simple sanitation
        String safeComment = comment.replace("'", "''");

        // 1. insert the new review
        String insertQuery = "INSERT INTO review (orderID, laundromatID, custID, rating, comment, createdAt) VALUES ("
                + orderID + ", "
                + laundromatID + ", "
                + custID + ", "
                + rating + ", '"
                + safeComment + "', "
                + "NOW()"
                + ")";

        try (Statement st = connection.createStatement()) {
            int rowsAffected = st.executeUpdate(insertQuery);
            if (rowsAffected == 0) {
                return false; // insert failed
            }

            // 2. if insert succeeded, recalculate the average rating for that laundromat
            String updateQuery = "UPDATE laundromat l " +
                    "JOIN ( " +
                    "    SELECT laundromatID, AVG(rating) AS avg_rating " +
                    "    FROM review " +
                    "    WHERE laundromatID = " + laundromatID + " " +
                    "    GROUP BY laundromatID " +
                    ") r ON l.laundromatID = r.laundromatID " +
                    "SET l.rating = r.avg_rating " +
                    "WHERE l.laundromatID = " + laundromatID;

            st.executeUpdate(updateQuery);
            return true; // success

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a review already exists for a specific order.
     *
     */
    public boolean checkIfReviewExists(int orderID) {
        if (connection == null) {
            return false;
        }
        String query = "SELECT 1 FROM review WHERE orderID = " + orderID + " LIMIT 1";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            return rs.next(); // true if a row is found
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets an existing review (and the reviewer's name) by the order ID.
     *
     */
    public ReviewData getReviewByOrderId(int orderID) {
        if (connection == null) {
            return null;
        }

        // join review with customer to get the username
        String query = "SELECT r.*, c.custUsername " +
                "FROM review r " +
                "JOIN customer c ON r.custID = c.custID " +
                "WHERE r.orderID = " + orderID + " " +
                "LIMIT 1";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            if (rs.next()) {
                // if a review is found, create the object
                return new ReviewData(
                        rs.getInt("reviewID"),
                        rs.getInt("custID"),
                        rs.getString("custUsername"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getTimestamp("createdAt").toString()
                );
            } else {
                return null; // no review found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}