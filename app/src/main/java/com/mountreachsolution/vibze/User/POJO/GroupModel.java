package com.mountreachsolution.vibze.User.POJO;

public class GroupModel {
    String groupId;
     String groupName;

    public GroupModel(String groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }
}
