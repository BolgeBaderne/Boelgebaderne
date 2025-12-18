package com.example.bolgebaderne.dto;

import com.example.bolgebaderne.model.WaitlistType;

public class WaitlistJoinRequest {
    private int userId;
    private WaitlistType type;

    public WaitlistJoinRequest() {}

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public WaitlistType getType() {
        return type;
    }

    public void setType(WaitlistType type) {
        this.type = type;
    }
}
