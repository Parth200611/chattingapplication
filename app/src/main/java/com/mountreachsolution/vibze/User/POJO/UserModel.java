package com.mountreachsolution.vibze.User.POJO;

public class UserModel {
    private String name, username, image;
    private boolean isSelected;

    public UserModel(String name, String username, String image) {
        this.name = name;
        this.username = username;
        this.image = image;
        this.isSelected = false;
    }

    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getImage() { return image; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}
