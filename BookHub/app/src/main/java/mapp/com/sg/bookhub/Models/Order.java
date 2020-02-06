package mapp.com.sg.bookhub.Models;

public class Order {

    private String postId;
    private String createdBy;
    private String createdAt;

    public Order(String postId, String createdBy, String createdAt) {
        this.postId = postId;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


}
