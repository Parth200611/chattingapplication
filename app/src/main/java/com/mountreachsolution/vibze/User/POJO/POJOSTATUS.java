package com.mountreachsolution.vibze.User.POJO;

public class POJOSTATUS {
    String id,username,image;

    public POJOSTATUS(String id, String username, String image) {
        this.id = id;
        this.username = username;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
