package com.svs.hztb.Bean;

import java.util.ArrayList;

/**
 * Created by VenuNalla on 7/6/16.
 */
public class RegisteredUserInput {

    ArrayList<ContactBean> userProfileRequests ;

    public ArrayList<ContactBean> getUserProfileRequests() {
        return userProfileRequests;
    }

    public void setUserProfileRequests(ArrayList<ContactBean> userProfileRequests) {
        this.userProfileRequests = userProfileRequests;
    }
}
