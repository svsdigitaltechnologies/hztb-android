package com.svs.hztb.RealmDatabase;

import io.realm.RealmModel;
import io.realm.RealmObject;

/**
 * Created by VenuNalla on 7/27/16.
 */
public class RealmOpinionCountData extends RealmObject {
    private int userId;
    private int givenCount;
    private int pendingCount;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGivenCount() {
        return givenCount;
    }

    public void setGivenCount(int givenCount) {
        this.givenCount = givenCount;
    }

    public int getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(int pendingCount) {
        this.pendingCount = pendingCount;
    }
}
