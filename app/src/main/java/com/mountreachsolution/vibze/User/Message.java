package com.mountreachsolution.vibze.User;

public class Message {
    String senderId;
     String message;
     String timestamp;String image;

    public Message(String senderId, String message, String timestamp, String image) {
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
        this.image = image;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public boolean hasImage() {
        return image != null && !image.isEmpty();
    }
}
