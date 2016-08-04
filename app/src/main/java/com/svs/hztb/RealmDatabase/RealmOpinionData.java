package com.svs.hztb.RealmDatabase;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by VenuNalla on 7/7/16.
 */
public class RealmOpinionData extends RealmObject {
    @PrimaryKey
    private int opinionId;
    //private int requestedUserId;
    private int requestedGroupId;
    private String productName;

    public RealmProduct getProduct() {
        return product;
    }

    public void setProduct(RealmProduct product) {
        this.product = product;
    }

    private String selfieUrl;

    public String getSelfieUrl() {
        return selfieUrl;
    }

    public void setSelfieUrl(String selfieUrl) {
        this.selfieUrl = selfieUrl;
    }

    public RealmProduct product;
    public RealmList<RealmResponseCount> getResponseCountList() {
        return responseCountList;
    }

    public void setResponseCountList(RealmList<RealmResponseCount> responseCountList) {
        this.responseCountList = responseCountList;
    }

    public RealmList<RealmResponseCount> responseCountList;

    public int getOpinionId() {
        return opinionId;
    }
    public void setOpinionId(int opinionId) {
        this.opinionId = opinionId;
    }

    public int getRequestedGroupId() {
        return requestedGroupId;
    }
    public void setRequestedGroupId(int requestedGroupId) {
        this.requestedGroupId = requestedGroupId;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }



}
