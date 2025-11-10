package org.example.models;

public class ReviewData {
    private int reviewID;
    private int custID;
    private String custUsername;
    private int rating;
    private String comment;
    private String createdAt;

    public ReviewData(int reviewID, int custID, String custUsername, int rating, String comment, String createdAt) {
        this.reviewID = reviewID;
        this.custID = custID;
        this.custUsername = custUsername;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // getters
    public String getCustUsername() {
        return custUsername;
    }
    public String getComment() {
        return comment;
    }
    public int getRating() {
        return rating;
    }
}