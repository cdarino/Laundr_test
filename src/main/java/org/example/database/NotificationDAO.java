package org.example.database;

import org.example.models.NotificationData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

/**
 * dao for notification-related queries.
 * part of the observer pattern.
 */
public class NotificationDAO {

    private Connection connection;

    public NotificationDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * creates a new notification for a user.
     * this is called by orderdao (the "subject") when a state changes.
     */
    public boolean createNotification(int custID, String message) {
        if (connection == null) {
            System.err.println("notificationdao: cannot create notification, no connection.");
            return false;
        }

        // simple sanitation
        String safeMessage = message.replace("'", "''");

        String query = "INSERT INTO notification (custID, message, createdAt) VALUES ("
                + custID + ", '"
                + safeMessage + "', "
                + "NOW()"
                + ")";

        try (Statement st = connection.createStatement()) {
            int rowsAffected = st.executeUpdate(query);
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * fetches all unread notifications for a specific customer.
     */
    public Vector<NotificationData> getUnreadNotifications(int custID) throws SQLException {
        Vector<NotificationData> notifications = new Vector<>();
        if (connection == null) {
            throw new SQLException("cannot fetch notifications, no connection.");
        }

        String query = "SELECT * FROM notification " +
                "WHERE custID = " + custID + " AND isRead = 0 " +
                "ORDER BY createdAt DESC";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                notifications.add(new NotificationData(
                        rs.getInt("notificationID"),
                        rs.getString("message"),
                        rs.getTimestamp("createdAt").toString(),
                        rs.getBoolean("isRead")
                ));
            }
        }
        return notifications;
    }

    /**
     * marks a specific notification as read.
     */
    public boolean markAsRead(int notificationID) {
        if (connection == null) {
            System.err.println("notificationdao: cannot mark as read, no connection.");
            return false;
        }

        String query = "UPDATE notification SET isRead = 1 WHERE notificationID = " + notificationID;

        try (Statement st = connection.createStatement()) {
            int rowsAffected = st.executeUpdate(query);
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}