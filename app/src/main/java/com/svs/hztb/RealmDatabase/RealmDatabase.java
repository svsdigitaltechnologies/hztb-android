package com.svs.hztb.RealmDatabase;

import android.util.Log;

import com.svs.hztb.Bean.GroupDetail;
import com.svs.hztb.Bean.OpinionData;
import com.svs.hztb.Bean.Product;
import com.svs.hztb.Bean.UserProfileResponse;
import com.svs.hztb.Bean.UserProfileResponses;
import com.svs.hztb.Interfaces.ContactsSyncCompleted;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


/**
 * Created by VenuNalla on 7/9/16.
 */
public class RealmDatabase {

    private ContactsSyncCompleted syncCompleted;
    private Realm realm;
    private int size;
    public RealmDatabase(){
        realm = Realm.getDefaultInstance();
    }

    public RealmDatabase(ContactsSyncCompleted contactsSyncCompleted){
        realm = Realm.getDefaultInstance();
        syncCompleted = contactsSyncCompleted;
    }

    public RealmResults getAllContactsWithUserIDs() {
        RealmResults<RealmUserProfileResponse> contactsWithUserIDs = realm.where(RealmUserProfileResponse.class).findAll();
        return contactsWithUserIDs;
    }


    public void storeUserIds(final List<UserProfileResponse> userProfileResponsesArrayList){

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
                    size = size+1;
                    Log.d("Size"+size,"List size"+userProfileResponsesArrayList.size());
                    if (userProfileResponsesArrayList.size() == size){
                        if (syncCompleted != null) {
                            syncCompleted.onContactsSyncCompleted();
                        }
                    }
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



    public ArrayList<OpinionData> getAllOpinions() {
        RealmResults<RealmOpinionData> opinionList = realm.where(RealmOpinionData.class).findAll();
        ArrayList<OpinionData> opinionDataArrayList = new ArrayList<>();
        Iterator<RealmOpinionData> iterator = opinionList.iterator();
        while (iterator.hasNext()){
            RealmOpinionData realmOpinionData= iterator.next();
            OpinionData opinionData = new OpinionData();
            opinionData.setOpinionId(realmOpinionData.getOpinionId());
            opinionData.setProductName(realmOpinionData.getProductName());
            opinionData.setRequestedGroupId(realmOpinionData.getRequestedGroupId());
            HashMap<String,Integer> responseCount = new HashMap<>();
            Iterator<RealmResponseCount> responseIterator = realmOpinionData.getResponseCountList().iterator();
            while (responseIterator.hasNext()){
                RealmResponseCount count = responseIterator.next();
                responseCount.put(count.getResponseType(),count.getResponseCount());
            }
            opinionData.setResponseCounts(responseCount);
            RealmProduct realmProduct = realmOpinionData.getProduct();
            Product product = new Product();
            product.setName(realmProduct.getName());
            product.setImageUrl(realmProduct.getImageUrl());
            product.setLongDesc(realmProduct.getLongDesc());
            product.setShortDesc(realmProduct.getShortDesc());
            product.setPrice(realmProduct.getPrice());
            opinionData.setProduct(product);
            opinionDataArrayList.add(opinionData);
        }
        return opinionDataArrayList;
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
        RealmResults<GroupDetailRealm> groupList = realm.where(GroupDetailRealm.class).findAll();
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
                        dataToStore.getUserDataList().clear();
                        dataToStore.getUserDataList().addAll(groupDetailRealm.getUserDataList());
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
                    Log.d("Failure", error.toString());

                }
            });
        }
    }
}

