package com.svs.hztb.Bean;

public class RefreshInput {
    private int userId;
    private int opinionId;
    private String lastUpdatedTime;

    public String getResponderUserId() {
        return responderUserId;
    }

    public void setResponderUserId(String responderUserId) {
        this.responderUserId = responderUserId;
    }

    private String responderUserId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public int getOpinionId() {
        return opinionId;
    }

    public void setOpinionId(int opinionId) {
        this.opinionId = opinionId;
    }


}