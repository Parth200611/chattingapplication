package com.mountreachsolution.vibze.User.POJO;

public class ChatMessage {
    private String sender, receiver, message, timestamp, imageUrl;

    public ChatMessage(String sender, String receiver, String message, String timestamp, String imageUrl) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
    }

    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
    public String getImageUrl() { return imageUrl; }
}
