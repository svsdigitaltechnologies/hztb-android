package com.svs.hztb.Fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.svs.hztb.Adapters.ContactsAdapter;
import com.svs.hztb.Bean.Contact;
import com.svs.hztb.Bean.GroupDetail;
import com.svs.hztb.Bean.Product;
import com.svs.hztb.Bean.RequestOpinionInput;
import com.svs.hztb.Bean.RequestOpinionOutput;
import com.svs.hztb.Bean.Status;
import com.svs.hztb.Bean.UserData;
import com.svs.hztb.CustomViews.WalkWayButton;
import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.R;
import com.svs.hztb.RealmDatabase.RealmDatabase;
import com.svs.hztb.RealmDatabase.RealmUserProfileResponse;
import com.svs.hztb.RestService.ErrorStatus;
import com.svs.hztb.RestService.OpinionService;
import com.svs.hztb.RestService.ServiceGenerator;
import com.svs.hztb.Utils.ContactsSync;
import com.svs.hztb.Utils.LoadingBar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.realm.RealmList;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GroupsFragment extends Fragment {

    private ArrayList<Contact> contactList;
    private ListView contactsListView;
    private ContactsAdapter adapter;
    EditText contactSearch;
    private Button doneButton;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2001;
    protected LoadingBar _loader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contactSearch =(EditText)view.findViewById(R.id.edittext_search_contact);
        contactsListView = (ListView)view.findViewById(R.id.listview_contacts);
        doneButton = (Button)view.findViewById(R.id.doneButton);
        _loader=new LoadingBar(getActivity());
        contactList = new ArrayList<>();
        Button refreshButton = (Button)view.findViewById(R.id.button_refresh);
        refreshButton.setVisibility(View.VISIBLE);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactsSync syncCont = new ContactsSync(getActivity());
                syncCont.syncContactsToServer();
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDoneButtonClicked();
            }
        });
        initviews();
//        checkIfPermissionIsGranted();

    }

    public void showLoader(){
        if(_loader!=null && !_loader.isShowing()){
            _loader.show();
        }
    }

    public void cancelLoader(){
        if(_loader!=null && _loader.isShowing()){
            _loader.cancel();
        }
    }




    private void initviews() {

        RealmList<RealmUserProfileResponse> contactsListWithUserIds = new RealmList<>();
        contactsListWithUserIds.addAll(new RealmDatabase().getAllContactsWithUserIDs());
        Iterator<RealmUserProfileResponse> responseIterator = contactsListWithUserIds.iterator();
        while (responseIterator.hasNext())
        {
            RealmUserProfileResponse userProfileResponse =responseIterator.next();
            Contact contact = new Contact();
            contact.setNumber(userProfileResponse.getMobileNumber());
            contact.setUserId(userProfileResponse.getUserId());
            contact.setContactName(userProfileResponse.getName());
            contact.setContactImagePath(userProfileResponse.getProfilePictureURL());
            contact.setUserRegistered(true);
            contactList.add(contact);
        }


        adapter = new ContactsAdapter(getActivity().getApplicationContext(),contactList);
        contactsListView.setAdapter(adapter);
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (contactList.get(i).isSelected()) {
                    contactList.get(i).setSelected(false);
                }else contactList.get(i).setSelected(true);

                adapter.notifyDataSetChanged();
            }
        });

        contactSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                GroupsFragment.this.adapter.filter(""+cs);
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
    }


    public void onDoneButtonClicked(){
        ArrayList<Contact> contactsSelected = getSelectedContactsList();
        if (contactsSelected.size() > 0) {
            showAlertDialog(contactsSelected);
        }else {
            Toast.makeText(getActivity().getApplicationContext(),"Please Select Contact",Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<Contact> getSelectedContactsList() {
        ArrayList<Contact> selectedList = new ArrayList<>();
        Iterator<Contact> iterator = contactList.iterator();
        while (iterator.hasNext()){
            Contact contactItem = iterator.next();
            if (contactItem.isSelected() == true){
                Cursor phones = getActivity().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactItem.getContactId(),
                        null, null);
                while (phones.moveToNext()) {
                    String phoneNumber = phones.getString(
                            phones.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contactItem.setNumber(phoneNumber);
                }
                phones.close();
                selectedList.add(contactItem);
            }
        }
        return selectedList;
    }

    private void showAlertDialog(ArrayList<Contact> contactsSelected) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.custom_group_selection_add_alert, null);
        dialogBuilder.setView(dialog);

        final AlertDialog alertDialog = dialogBuilder.create();

        TextView contectText = (TextView) dialog.findViewById(R.id.textview_content);
        final EditText groupName = (EditText)dialog.findViewById(R.id.edittext_groupName);
        String contactString = null;
        if (contactsSelected.size()>1){
            contactString = "Contacts";
        }else {
            contactString = "Contact";
        }

        contectText.setText("Do you want to request with  "+contactsSelected.size()+" "+contactString+"");

        WalkWayButton addButton = (WalkWayButton)dialog.findViewById(R.id.button_add_groupName);
        WalkWayButton sendButton = (WalkWayButton)dialog.findViewById(R.id.button_add_sendButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postDataForNewRequest();
                alertDialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void postDataForNewRequest() {
        showLoader();

        OpinionService opinionService = new OpinionService();
        GroupDetail groupDetail = new GroupDetail();
        groupDetail.setUserId(Integer.valueOf(new AppSharedPreference().getUserID(getActivity().getApplicationContext())));

        RequestOpinionInput requestOpinionInput = new RequestOpinionInput();
        requestOpinionInput.setRequesterUserId(Integer.valueOf(new AppSharedPreference().getUserID(getActivity().getApplicationContext())));
        Product product = new Product();
        product.setName("Wrwlcome");
        product.setShortDesc("aa");
        product.setLongDesc("fe");
        product.setImageUrl("c:/ada/asdasd");
        product.setPrice(24.0);
        requestOpinionInput.setProduct(product);
        List<UserData> userIDs = new ArrayList<>();
        ArrayList<Contact> contactsSelected = getSelectedContactsList();
        Iterator<Contact> contactIterator = contactsSelected.iterator();
        while (contactIterator.hasNext()){
            Contact contact = contactIterator.next();
            if (contact.isSelected()){
                UserData userData = new UserData();
                userData.setUserId(Integer.valueOf(contact.getUserId()));
                userIDs.add(userData);
            }
        }
//        requestOpinionInput.setRequestedUserIds(userIDs);

        Log.i(getActivity().getPackageName(),requestOpinionInput.toString());

        Observable<Response<RequestOpinionOutput>> registerResponseObservable = opinionService.requestOpinionForNewProduct(requestOpinionInput);

        registerResponseObservable.observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).subscribe(new Subscriber<Response<RequestOpinionOutput>>() {
            @Override
            public void onCompleted() {
                cancelLoader();
            }

            @Override
            public void onError(Throwable e) {
                cancelLoader();
            }

            @Override
            public void onNext(Response<RequestOpinionOutput> requestResponse) {

                if (requestResponse.isSuccessful()) {

                    if (requestResponse.body().getStatus() == Status.SUCCESS){
                        Toast.makeText(getActivity().getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getActivity().getApplicationContext(),"UNSUCCESSFUL",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    List<ErrorStatus> listErrorStatus = ServiceGenerator.parseErrorBody(requestResponse);
                    for (ErrorStatus listErrorState : listErrorStatus) {
                        Log.d("Error Message : ", listErrorState.getMessage() + " " + listErrorState.getStatus());
                    }
                }
            }
        });
    }
}
