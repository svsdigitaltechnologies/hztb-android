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

    public RealmList<RealmInt> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(RealmList<RealmInt> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public RealmList<RealmInt> groupMembers;
    @PrimaryKey
    private int groupId;

    public boolean isSelect() {
        return isSelect;
    }
    public void setSelect(boolean select) {
        isSelect = select;
    }

    private boolean isSelect;
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
}
