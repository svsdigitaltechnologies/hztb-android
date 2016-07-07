package com.svs.hztb.RealmDatabase;

import android.util.Log;

import java.util.Iterator;

import io.realm.Realm;
import io.realm.RealmIntRealmProxyInterface;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.internal.async.QueryUpdateTask;

/**
 * Created by VenuNalla on 7/6/16.
 */
public class GroupRepositoryDatabase {

    public void getAllGroupList(){
        final Realm realm = Realm.getDefaultInstance();

        RealmResults<GroupDetailRealm> users = realm.where(GroupDetailRealm.class).findAll();

    }


    public void addGroupsListToDatabase(final RealmList<GroupDetailRealm> groupDetailRealmsList)
    {

        Iterator<GroupDetailRealm> iterator = groupDetailRealmsList.iterator();
        while (iterator.hasNext()){
            final GroupDetailRealm groupDetailRealm = iterator.next();


        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                GroupDetailRealm dataToStore = bgRealm.where(GroupDetailRealm.class).equalTo("groupId",groupDetailRealm.getGroupId()).findFirst();
                if (dataToStore == null) {
                    dataToStore = bgRealm.createObject(GroupDetailRealm.class);
                    dataToStore.setGroupId(groupDetailRealm.getGroupId());
                    dataToStore.setGroupName(groupDetailRealm.getGroupName());
                    dataToStore.groupMembers.clear();
                    dataToStore.groupMembers.addAll(groupDetailRealm.getGroupMembers());
                }
                else {
                    int i = dataToStore.getGroupId();
                    String name = dataToStore.getGroupName();
                    Log.d("GroupId"+i+"   ",name);

                    dataToStore.groupMembers.clear();
                    dataToStore.groupMembers.addAll(groupDetailRealm.getGroupMembers());
                    dataToStore.setGroupName(groupDetailRealm.getGroupName());
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
