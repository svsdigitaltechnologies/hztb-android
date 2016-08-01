package com.svs.hztb.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svs.hztb.Bean.Contact;
import com.svs.hztb.Bean.Product;
import com.svs.hztb.Bean.RequestOpinionInput;
import com.svs.hztb.Bean.RequestOpinionOutput;
import com.svs.hztb.Bean.Status;
import com.svs.hztb.Bean.UserProfileRequest;
import com.svs.hztb.Bean.UserProfileRequests;
import com.svs.hztb.Bean.UserProfileResponse;
import com.svs.hztb.Bean.UserProfileResponses;
import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.Interfaces.ContactsSyncCompleted;
import com.svs.hztb.R;
import com.svs.hztb.RealmDatabase.RealmContact;
import com.svs.hztb.RealmDatabase.RealmDatabase;
import com.svs.hztb.RestService.ErrorStatus;
import com.svs.hztb.RestService.OpinionService;
import com.svs.hztb.RestService.RegisterService;
import com.svs.hztb.RestService.ServiceGenerator;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.realm.RealmList;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by VenuNalla on 7/9/16.
 */
public class ContactsSync {
    private Activity activity;
//    private RealmList<RealmContact> contactList;

    private ContactsSyncCompleted contactsSyncCompleted;
    private ArrayList<UserProfileRequest> userProfileRequestList;

    public ContactsSync(Activity activity1){
        this.activity = activity1;
    }

    public ContactsSync(ContactsSyncCompleted syncCompleted,Activity activitySync){
        this.contactsSyncCompleted = syncCompleted;
        this.activity = activitySync;
    }

    public void syncContactsToServer(){
        checkIfPermissionIsGranted();
    }

    public void removeCallBack(){
        contactsSyncCompleted = null;
    }

    public void checkIfPermissionIsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                readContactInBackground();
            }
            else {
                requestReadPhoneStatePermission();
            }
        }else {
            readContactInBackground();
        }
    }

    public void doPermissionGrantedStuffs() {
        readContactInBackground();
    }


    private void searchForContacts() {
        userProfileRequestList = new ArrayList<>();
//        contactList = new RealmList<>();
        ContentResolver cr = activity.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
//                RealmContact contact = new RealmContact();
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
//                contact.setContactName(name);
//                contact.setContactImagePath(cur
//                        .getString(cur
//                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    {
                        Cursor phones = activity.getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                null, null);
                        while (phones.moveToNext()) {
                            String phoneNumber = phones.getString(
                                    phones.getColumnIndex(
                                            ContactsContract.CommonDataKinds.Phone.NUMBER)).replace("+","").replace(" ","").replace("(","").replace(")","").replace("-","");
 //                           contact.setContactNumber(phoneNumber);
                            UserProfileRequest userRequest = new UserProfileRequest();
                            userRequest.setMobileNumber(phoneNumber);
                            userProfileRequestList.add(userRequest);
                        }
                        phones.close();
 //                       contactList.add(contact);
                    }
                }
            }
        }

        postDataForUserIds();


    }

    private void postDataForUserIds() {

        Set set = new TreeSet(new Comparator<UserProfileRequest>() {
            @Override
            public int compare(UserProfileRequest req1, UserProfileRequest req2) {
                if (req1.getMobileNumber().equals(req2.getMobileNumber())) {
                    return 0;
                }
                return 1;
            }
        });
        set.addAll(userProfileRequestList);
        userProfileRequestList.clear();
        userProfileRequestList = new ArrayList<>(set);

        UserProfileRequests userprofileRequest = new UserProfileRequests();
        userprofileRequest.setUserProfileRequests(userProfileRequestList);

        RegisterService registerService = new RegisterService();

        Observable<Response<UserProfileResponses>> getRegisterdUsersObservable = registerService.getRegisteredUserList(userprofileRequest);

        getRegisterdUsersObservable.observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).subscribe(new Subscriber<Response<UserProfileResponses>>() {
            @Override
            public void onCompleted() {
                Log.d(activity.getLocalClassName().toString(),"Sync Completed");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(activity.getLocalClassName().toString(),"Sync Error");
            }

            @Override
            public void onNext(Response<UserProfileResponses> response) {

                if (response.isSuccessful()) {
                    List<UserProfileResponse> userProfileResponses= new ArrayList<UserProfileResponse>();
                    userProfileResponses.addAll(response.body().getUserProfileResponses());
                    RealmDatabase db = new RealmDatabase (contactsSyncCompleted);
                    db.storeUserIds(userProfileResponses);
                }
            }
        });


    }



    private void readContactInBackground() {
        Thread fetchContactsThread = new Thread((new Runnable() {
            @Override
            public void run() {
                searchForContacts();

            }
        }));
        fetchContactsThread.start();

    }


    /**
     * Requests the READ_PHONE_STATE permission.
     * If the permission has been denied previously, a dialog will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.READ_CONTACTS)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            new AlertDialog.Builder(activity)
                    .setTitle("Permission Request")
                    .setMessage(activity.getString(R.string.permission_read_phone_state_rationale))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //re-request
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{Manifest.permission.READ_CONTACTS},
                           Constants.MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    })
                    .show();
        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CONTACTS},
           Constants.MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

}

