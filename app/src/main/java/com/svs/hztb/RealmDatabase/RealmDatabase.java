package com.svs.hztb.RealmDatabase;

import android.util.Log;

import com.svs.hztb.Bean.GroupDetail;
import com.svs.hztb.Bean.OpinionCountData;
import com.svs.hztb.Bean.OpinionData;
import com.svs.hztb.Bean.Product;
import com.svs.hztb.Bean.UserData;
import com.svs.hztb.Bean.UserProfileResponse;
import com.svs.hztb.Bean.UserProfileResponses;
import com.svs.hztb.Interfaces.ContactsSyncCompleted;
import com.svs.hztb.Interfaces.IRealmDataStoredCallBack;

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
    private IRealmDataStoredCallBack dataStoredCallBack;
    public RealmDatabase(){
        realm = Realm.getDefaultInstance();
    }

    public RealmDatabase(ContactsSyncCompleted contactsSyncCompleted){
        realm = Realm.getDefaultInstance();
        size = 0;
        syncCompleted = contactsSyncCompleted;
    }
    public RealmDatabase(IRealmDataStoredCallBack iRealmDataStoredCallBack){
        realm = Realm.getDefaultInstance();
        dataStoredCallBack = iRealmDataStoredCallBack;
        size = 0;
    }

    public void removeIDataStoredCallBack(){
        dataStoredCallBack = null;
    }

    public void removeIContactsSyncCompletedCallBack(){
        syncCompleted = null;
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
                            size = 0;
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
            opinionData.setSelfieUrl(realmOpinionData.getSelfieUrl());
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

    public ArrayList<OpinionCountData> getAllOpinionsGiven() {
        RealmResults<RealmOpinionCountData> opinionList = realm.where(RealmOpinionCountData.class).findAll();
        ArrayList<OpinionCountData> opinionGivenDataArrayList = new ArrayList<>();
        Iterator<RealmOpinionCountData> iterator = opinionList.iterator();
        while (iterator.hasNext()){
            RealmOpinionCountData realmOpinionData= iterator.next();
            OpinionCountData opinionCountData = new OpinionCountData();
            opinionCountData.setUserId(realmOpinionData.getUserId());
            opinionCountData.setPendingCount(realmOpinionData.getPendingCount());
            opinionCountData.setGivenCount(realmOpinionData.getGivenCount());
            HashMap<String,Integer> responseCount = new HashMap<>();
            opinionGivenDataArrayList.add(opinionCountData);
        }
        return opinionGivenDataArrayList;
    }

    public  void addRealmOpinionCountData(final RealmList<RealmOpinionCountData> realmOpinionCountDatas){
        Iterator<RealmOpinionCountData> iterator = realmOpinionCountDatas.iterator();
        while (iterator.hasNext()) {
            final RealmOpinionCountData realmOpinionCountData = iterator.next();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        RealmOpinionCountData dataToStore = bgRealm.where(RealmOpinionCountData.class).equalTo("userId", realmOpinionCountData.getUserId()).findFirst();
                        if (dataToStore == null) {
                            dataToStore = bgRealm.createObject(RealmOpinionCountData.class);
                            dataToStore.setUserId(realmOpinionCountData.getUserId());
                        }
                       dataToStore.setPendingCount(realmOpinionCountData.getPendingCount());
                        dataToStore.setGivenCount(realmOpinionCountData.getGivenCount());

                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        size += 1;
                        if (size == realmOpinionCountDatas.size()){
                            if (dataStoredCallBack != null) {
                                dataStoredCallBack.dataSuccessfullyStore(true);
                            }
                        }
                        Log.d("Success", "Success");
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        Log.d("Failure", "Failure");
                        if (dataStoredCallBack != null) {
                            dataStoredCallBack.dataSuccessfullyStore(false);
                        }
                    }
                });
        }
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
                        dataToStore.setSelfieUrl(realmOpinionData.getSelfieUrl());
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
                    size += 1;
                    if (size == realmOpinionDataRealmList.size()){
                        if (dataStoredCallBack != null) {
                            dataStoredCallBack.dataSuccessfullyStore(true);
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

    public ArrayList<GroupDetail> getAllGroupList(){
        ArrayList<GroupDetail> groupDetailsList = new ArrayList<GroupDetail>();
        RealmResults<GroupDetailRealm> groupList = realm.where(GroupDetailRealm.class).findAll();
        Iterator<GroupDetailRealm> iterator = groupList.iterator();
        while (iterator.hasNext()){
            GroupDetailRealm realmGroup = iterator.next();
            GroupDetail groupDetail = new GroupDetail();
            groupDetail.setUserId(realmGroup.getUserId());
            groupDetail.setGroupId(realmGroup.getGroupId());
            groupDetail.setGroupName(realmGroup.getGroupName());
            Iterator<RealmUserData> realmUserDataIterator = realmGroup.getUserDataList().iterator();
            ArrayList<UserData> userDataArrayList = new ArrayList<>();
            while (realmUserDataIterator.hasNext()){
                RealmUserData realmUserData = realmUserDataIterator.next();
                UserData userData = new UserData();
                userData.setUserId(realmUserData.getUserId());
                userData.setFirstName(realmUserData.getFirstName());
                userData.setLastname(realmUserData.getLastname());
                userDataArrayList.add(userData);
            }
            groupDetail.setGroupMembers(userDataArrayList);
            groupDetailsList.add(groupDetail);
        }
        return groupDetailsList;
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
                    size = size+1;
                    Log.d("Size"+size,"List size"+groupDetailRealmsList.size());
                    if (groupDetailRealmsList.size() == size){
                        if (dataStoredCallBack != null) {
                            dataStoredCallBack.dataSuccessfullyStore(true);
                            size = 0;
                        }
                    }
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

