package com.svs.hztb.RealmDatabase;

import io.realm.RealmModel;
import io.realm.RealmObject;

/**
 * Created by VenuNalla on 7/9/16.
 */
public class RealmContact extends RealmObject {
    private String contactNumber;
    private String contactName;
    private String contactId;
    private String contactImagePath;

    public String getContactImagePath() {
        return contactImagePath;
    }

    public void setContactImagePath(String contactImagePath) {
        this.contactImagePath = contactImagePath;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }
}
