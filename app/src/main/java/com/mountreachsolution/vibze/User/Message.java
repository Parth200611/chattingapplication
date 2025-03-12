package com.mountreachsolution.vibze.User;

public class Message {
    String senderId;
     String message;
     String timestamp;
    public Message(String senderId, String message, String timestamp) {
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getSenderId() { return senderId; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
}
