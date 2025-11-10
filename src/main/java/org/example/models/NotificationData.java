package org.example.models;

/**
 * simple dto to hold data for one notification.
 */
public class NotificationData {
    private int notificationID;
    private String message;
    private String createdAt;
    private boolean isRead;

    public NotificationData(int notificationID, String message, String createdAt, boolean isRead) {
        this.notificationID = notificationID;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    // getters
    public int getNotificationID() {
        return notificationID;
    }
    public String getMessage() {
        return message;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public boolean isRead() {
        return isRead;
    }
}