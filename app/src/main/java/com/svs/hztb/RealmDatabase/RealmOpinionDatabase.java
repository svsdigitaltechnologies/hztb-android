package com.svs.hztb.RealmDatabase;

import android.util.Log;

import java.util.Iterator;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by VenuNalla on 7/8/16.
 */
public class RealmOpinionDatabase {

    public void getAllOpinions(){
        final Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmOpinionData> opinionList = realm.where(RealmOpinionData.class).findAll();
    }


    public void addRealmOpinionData(final RealmList<RealmOpinionData> realmOpinionDataRealmList)
    {

        Iterator<RealmOpinionData> iterator = realmOpinionDataRealmList.iterator();
        while (iterator.hasNext()){
            final RealmOpinionData realmOpinionData = iterator.next();


            final Realm realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    RealmOpinionData dataToStore = bgRealm.where(RealmOpinionData.class).equalTo("opinionId",realmOpinionData.getOpinionId()).findFirst();
                    if (dataToStore == null) {
                        dataToStore = bgRealm.createObject(RealmOpinionData.class);
                        dataToStore.setProductName(realmOpinionData.getProductName());
                        dataToStore.setRequestedGroupId(realmOpinionData.getRequestedGroupId());
                        dataToStore.getResponseCountList().addAll(realmOpinionData.getResponseCountList());
                        dataToStore.setOpinionId(realmOpinionData.getOpinionId());
                        RealmProduct realmProduct = bgRealm.createObject(RealmProduct.class);
                        realmProduct.setName(realmOpinionData.getProduct().getName());
                        realmProduct.setImageUrl(realmOpinionData.getProduct().getImageUrl());
                        realmProduct.setLongDesc(realmOpinionData.getProduct().getLongDesc());
                        realmProduct.setPrice(realmOpinionData.getProduct().getPrice());
                        realmProduct.setShortDesc(realmOpinionData.getProduct().getShortDesc());
                        dataToStore.setProduct(realmProduct);

//                        dataToStore.setProduct(realmOpinionData.getProduct());
                    }
                    else {
                        dataToStore.setProductName(realmOpinionData.getProductName());
                        dataToStore.setRequestedGroupId(realmOpinionData.getRequestedGroupId());
                        dataToStore.getResponseCountList().clear();
                        dataToStore.getResponseCountList().addAll(realmOpinionData.getResponseCountList());
//                        dataToStore.setProduct(realmOpinionData.getProduct());
                        RealmProduct realmProduct = bgRealm.createObject(RealmProduct.class);
                        realmProduct.setName(realmOpinionData.getProduct().getName());
                        realmProduct.setImageUrl(realmOpinionData.getProduct().getImageUrl());
                        realmProduct.setLongDesc(realmOpinionData.getProduct().getLongDesc());
                        realmProduct.setPrice(realmOpinionData.getProduct().getPrice());
                        realmProduct.setShortDesc(realmOpinionData.getProduct().getShortDesc());
                        dataToStore.setProduct(realmProduct);

                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Log.d("Success","Success");
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Log.d("Failure","Failure");

                }
            });
        }


        /*
        Iterator<GroupDetailRealm> iterator = groupDetailRealmsList.iterator();
        while (iterator.hasNext()){
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            GroupDetailRealm groupDetailRealm = iterator.next();
            GroupDetailRealm dataToStore = realm.where(GroupDetailRealm.class).equalTo("groupId",groupDetailRealm.getGroupId()).findFirst();
            if (dataToStore == null){
                dataToStore = realm.createObject(GroupDetailRealm.class);
                dataToStore.setGroupId(groupDetailRealm.getGroupId());
                dataToStore.setGroupName(groupDetailRealm.getGroupName());
                dataToStore.setGroupMembers(groupDetailRealm.getGroupMembers());
            }else {
                dataToStore.setGroupMembers(groupDetailRealm.getGroupMembers());
                dataToStore.setGroupName(groupDetailRealm.getGroupName());
            }
            realm.commitTransaction();
        }
        */
    }
}
