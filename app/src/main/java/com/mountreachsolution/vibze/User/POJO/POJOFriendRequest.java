package com.mountreachsolution.vibze.User.POJO;

public class POJOFriendRequest {
    String senderUsername;

    public POJOFriendRequest(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }
}
