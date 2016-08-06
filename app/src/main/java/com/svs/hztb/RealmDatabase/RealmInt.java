package com.svs.hztb.RealmDatabase;

import io.realm.RealmObject;

public class RealmInt extends RealmObject {
    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    private int val;

}