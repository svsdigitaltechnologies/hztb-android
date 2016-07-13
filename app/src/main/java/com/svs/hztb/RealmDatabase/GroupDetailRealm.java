package com.svs.hztb.RealmDatabase;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by VenuNalla on 7/6/16.
 */
public class GroupDetailRealm extends RealmObject {
    @Required
    private String groupName;
    public RealmList<RealmUserData> userDataList;
    @PrimaryKey
    private int groupId;
    private boolean isSelect;

    public RealmList<RealmUserData> getUserDataList() {
        return userDataList;
    }

    public void setUserDataList(RealmList<RealmUserData> userDataList) {
        this.userDataList = userDataList;
    }

    public boolean isSelect() {
        return isSelect;
    }
    public void setSelect(boolean select) {
        isSelect = select;
    }
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public int getGroupId() {
        return groupId;
    }
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "GroupDetailRealm{" +
                "groupName='" + groupName + '\'' +
                ", userDataList=" + userDataList +
                ", groupId=" + groupId +
                ", isSelect=" + isSelect +
                '}';
    }
}
