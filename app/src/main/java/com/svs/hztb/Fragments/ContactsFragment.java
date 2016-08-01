package com.svs.hztb.Fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
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

import com.svs.hztb.Adapters.ContactsAdapter;
import com.svs.hztb.Bean.Contact;
import com.svs.hztb.Bean.Product;
import com.svs.hztb.Bean.RequestOpinionInput;
import com.svs.hztb.Bean.RequestOpinionOutput;
import com.svs.hztb.Bean.Status;
import com.svs.hztb.CustomViews.WalkWayButton;
import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.Interfaces.ContactsSyncCompleted;
import com.svs.hztb.R;
import com.svs.hztb.RealmDatabase.RealmDatabase;
import com.svs.hztb.RealmDatabase.RealmUserProfileResponse;
import com.svs.hztb.RestService.ErrorStatus;
import com.svs.hztb.RestService.OpinionService;
import com.svs.hztb.RestService.ServiceGenerator;
import com.svs.hztb.Utils.ContactsSync;
import com.svs.hztb.Utils.LoadingBar;

import java.util.ArrayList;
import android.os.Handler;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

import io.realm.RealmList;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ContactsFragment extends Fragment implements ContactsSyncCompleted{

    private ArrayList<Contact> contactList;
    private ListView contactsListView;
    private ContactsAdapter adapter;
    EditText contactSearch;
    private Button doneButton;
    byte[] imageData;
    private  ContactsSync contactsSync;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2001;
    protected LoadingBar _loader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment, container, false);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        imageData = bundle.getByteArray("imageData");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contactSearch =(EditText)view.findViewById(R.id.edittext_search_contact);
        contactsListView = (ListView)view.findViewById(R.id.listview_contacts);
        doneButton = (Button)view.findViewById(R.id.doneButton);
        _loader=new LoadingBar(getActivity());
        Button refreshButton = (Button)view.findViewById(R.id.button_refresh);
        refreshButton.setVisibility(View.VISIBLE);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              syncContacts();
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDoneButtonClicked();
            }
        });
        initviews();
    }

    private void syncContacts() {
        contactsSync = new ContactsSync(this,getActivity());
        contactsSync.syncContactsToServer();
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

        loadListViewDataFromDatabase();
        contactSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                ContactsFragment.this.adapter.filter(""+cs);
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

    private void loadListViewDataFromDatabase() {
        contactList = new ArrayList<>();
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
        RequestOpinionInput requestOpinionInput = new RequestOpinionInput();
        requestOpinionInput.setRequesterUserId(Integer.valueOf(new AppSharedPreference().getUserID(getActivity().getApplicationContext())));
        Product product = new Product();


        product.setName("Wrwlcome");
        product.setShortDesc("aa");
        product.setLongDesc("fe");
        product.setPrice(24.0);
        requestOpinionInput.setSelfiePic(imageData);
        requestOpinionInput.setProduct(product);
        List<Integer> userIDs = new ArrayList<>();
        ArrayList<Contact> contactsSelected = getSelectedContactsList();
        Iterator<Contact> contactIterator = contactsSelected.iterator();
        while (contactIterator.hasNext()){
            Contact contact = contactIterator.next();
            if (contact.isSelected()){
                userIDs.add(Integer.valueOf(contact.getUserId()));
            }
        }
        requestOpinionInput.setRequestedUserIds(userIDs);

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
                      getFragmentManager().popBackStack();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (contactsSync != null) {
            contactsSync.removeCallBack();
        }
    }

    @Override
    public void onContactsSyncCompleted() {
        loadListViewDataFromDatabase();
        Log.d("SyncCompleted","Syncing");
    }
}
