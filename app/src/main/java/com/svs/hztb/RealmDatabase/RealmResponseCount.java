package com.svs.hztb.RealmDatabase;

import io.realm.RealmObject;

/**
 * Created by VenuNalla on 7/8/16.
 */
public class RealmResponseCount extends RealmObject {
    private int responseCount;
    private String responseType;

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public int getResponseCount() {
        return responseCount;
    }

    public void setResponseCount(int responseCount) {
        this.responseCount = responseCount;
    }

}
