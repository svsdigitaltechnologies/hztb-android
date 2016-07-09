package com.svs.hztb.RealmDatabase;

import android.util.Log;

import com.svs.hztb.Bean.UserProfileResponse;
import com.svs.hztb.Bean.UserProfileResponses;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by VenuNalla on 7/9/16.
 */
public class RealmDatabase {

    private Realm realm;

    public RealmDatabase(){
        realm = Realm.getDefaultInstance();
    }

    public RealmResults getAllContactsWithUserIDs() {
        RealmResults<RealmUserProfileResponse> contactsWithUserIDs = realm.where(RealmUserProfileResponse.class).findAll();
        return contactsWithUserIDs;
    }



    public void storeUserIds(List<UserProfileResponse> userProfileResponsesArrayList){
        Iterator<UserProfileResponse>  iterator = userProfileResponsesArrayList.iterator();
        while (iterator.hasNext()){
            final UserProfileResponse userProfileResponse = iterator.next();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    RealmUserProfileResponse dataToStore = bgRealm.where(RealmUserProfileResponse.class).equalTo("mobileNumber", userProfileResponse.getMobileNumber()).findFirst();
                    if (dataToStore == null) {
                        dataToStore = bgRealm.createObject(RealmUserProfileResponse.class);
                        dataToStore.setMobileNumber(userProfileResponse.getMobileNumber());
                    }
                        dataToStore.setUserId(userProfileResponse.getUserId());
                        dataToStore.setProfilePictureURL(userProfileResponse.getProfilePictureURL());
                        dataToStore.setName(userProfileResponse.getName());

                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Log.d("Success", "Success");
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Log.d("Failure", "Failure");

                }
            });
        }

    }



    public void getAllOpinions() {
        RealmResults<RealmOpinionData> opinionList = realm.where(RealmOpinionData.class).findAll();
    }


    public void addRealmOpinionData(final RealmList<RealmOpinionData> realmOpinionDataRealmList) {

        Iterator<RealmOpinionData> iterator = realmOpinionDataRealmList.iterator();
        while (iterator.hasNext()) {
            final RealmOpinionData realmOpinionData = iterator.next();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    RealmOpinionData dataToStore = bgRealm.where(RealmOpinionData.class).equalTo("opinionId", realmOpinionData.getOpinionId()).findFirst();
                    if (dataToStore == null) {
                        dataToStore = bgRealm.createObject(RealmOpinionData.class);
                        dataToStore.setOpinionId(realmOpinionData.getOpinionId());
                    }
                        dataToStore.setProductName(realmOpinionData.getProductName());
                        dataToStore.setRequestedGroupId(realmOpinionData.getRequestedGroupId());
                        dataToStore.getResponseCountList().clear();
                        dataToStore.getResponseCountList().addAll(realmOpinionData.getResponseCountList());
                        RealmProduct realmProduct = bgRealm.createObject(RealmProduct.class);
                        realmProduct.setName(realmOpinionData.getProduct().getName());
                        realmProduct.setImageUrl(realmOpinionData.getProduct().getImageUrl());
                        realmProduct.setLongDesc(realmOpinionData.getProduct().getLongDesc());
                        realmProduct.setPrice(realmOpinionData.getProduct().getPrice());
                        realmProduct.setShortDesc(realmOpinionData.getProduct().getShortDesc());
                        dataToStore.setProduct(realmProduct);

                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Log.d("Success", "Success");
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Log.d("Failure", "Failure");

                }
            });
        }

    }

    public void getAllGroupList(){
        RealmResults<GroupDetailRealm> users = realm.where(GroupDetailRealm.class).findAll();
    }


    public void addGroupsListToDatabase(final RealmList<GroupDetailRealm> groupDetailRealmsList) {

        Iterator<GroupDetailRealm> iterator = groupDetailRealmsList.iterator();
        while (iterator.hasNext()) {
            final GroupDetailRealm groupDetailRealm = iterator.next();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    GroupDetailRealm dataToStore = bgRealm.where(GroupDetailRealm.class).equalTo("groupId", groupDetailRealm.getGroupId()).findFirst();
                    if (dataToStore == null) {
                        dataToStore = bgRealm.createObject(GroupDetailRealm.class);
                        dataToStore.setGroupId(groupDetailRealm.getGroupId());
                    }
                        dataToStore.groupMembers.clear();
                        dataToStore.groupMembers.addAll(groupDetailRealm.getGroupMembers());
                        dataToStore.setGroupName(groupDetailRealm.getGroupName());

                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Log.d("Success", "Success");
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Log.d("Failure", "Failure");

                }
            });
        }
    }
}

