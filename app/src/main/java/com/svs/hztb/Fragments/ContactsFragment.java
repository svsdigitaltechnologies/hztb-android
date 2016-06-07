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

import com.svs.hztb.Activities.ConfirmRegistration;
import com.svs.hztb.Adapters.ContactsAdapter;
import com.svs.hztb.Bean.Contact;
import com.svs.hztb.Bean.ContactGroup;
import com.svs.hztb.Bean.Product;
import com.svs.hztb.Bean.RegisterResponse;
import com.svs.hztb.Bean.RequestOpinionInput;
import com.svs.hztb.Bean.RequestOpinionOutput;
import com.svs.hztb.Bean.Status;
import com.svs.hztb.CustomViews.WalkWayButton;
import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.Database.DatabaseHandler;
import com.svs.hztb.R;
import com.svs.hztb.RestService.ErrorStatus;
import com.svs.hztb.RestService.RegisterService;
import com.svs.hztb.RestService.ServiceGenerator;
import com.svs.hztb.Utils.LoadingBar;

import java.util.ArrayList;
import android.os.Handler;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ContactsFragment extends Fragment {

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

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contactSearch =(EditText)view.findViewById(R.id.edittext_search_contact);
        contactsListView = (ListView)view.findViewById(R.id.listview_contacts);
        doneButton = (Button)view.findViewById(R.id.doneButton);
        _loader=new LoadingBar(getActivity());

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDoneButtonClicked();
            }
        });
        checkIfPermissionIsGranted();

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
    private void checkIfPermissionIsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                readContactInBackground();
            }
            else {
                requestReadPhoneStatePermission();
            }
        }else {
            readContactInBackground();
        }
    }

    private void readContactInBackground() {
        new Thread() {
            public void run() {
                searchForContactsAndDisplay();
                displayHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private Handler displayHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            initviews();
        }
    };


    private void searchForContactsAndDisplay() {
        contactList = new ArrayList<>();
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                Contact contact = new Contact();
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                contact.setContactName(name);
                contact.setContactId(id);
                contact.setContactImagePath(cur
                        .getString(cur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    contactList.add(contact);
                }
            }
        }
    }



    /**
     * Requests the READ_PHONE_STATE permission.
     * If the permission has been denied previously, a dialog will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_CONTACTS)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            new AlertDialog.Builder(getActivity())
                    .setTitle("Permission Request")
                    .setMessage(getString(R.string.permission_read_phone_state_rationale))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //re-request
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.READ_CONTACTS},
                                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    })
                    .show();
        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
            // Received permission result for READ_PHONE_STATE permission.est.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has been granted, proceed with displaying IMEI Number
                //alertAlert(getString(R.string.permision_available_read_phone_state));
                doPermissionGrantedStuffs();
            } else {
                alertAlert(getString(R.string.permissions_not_granted_read_phone_state));

            }
        }
    }

    private void alertAlert(String msg) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                })
                .show();
    }

    public void doPermissionGrantedStuffs() {
        readContactInBackground();
    }


    private void initviews() {
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


    public void onDoneButtonClicked(){
        ArrayList<Contact> contactsSelected = getSelectedContactsList();
        showAlertDialog(contactsSelected);
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

            contectText.setText("Do you want to add "+contactsSelected.size()+" "+contactString+" to the group");

            WalkWayButton addButton = (WalkWayButton)dialog.findViewById(R.id.button_add_groupName);
            WalkWayButton sendButton = (WalkWayButton)dialog.findViewById(R.id.button_add_sendButton);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!groupName.getText().toString().isEmpty()){
                        alertDialog.cancel();
                        postDataForNewRequest(groupName.getText().toString().trim());
                    }else  groupName.setError("Field cannot be left blank.");



                }
            });

            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                }
            });

            alertDialog.show();
    }

    private void postDataForNewRequest(final String groupName) {
        showLoader();

        RegisterService registerService = new RegisterService();
        RequestOpinionInput requestOpinionInput = new RequestOpinionInput();
        requestOpinionInput.setRequesterUserId(1);
        requestOpinionInput.setGroupName(groupName);
        Product product = new Product();
        product.setName("Test606163");
        product.setShortDesc("Awesome");
        product.setLongDesc("brilliant");
        product.setImageUrl("c:/ada/asdasd");
        product.setPrice(22.0);
        requestOpinionInput.setProduct(product);
        List<Integer> userIDs = new ArrayList<>();
        userIDs.add(2);
        userIDs.add(3);
        requestOpinionInput.setRequestedUserIds(userIDs);

        Log.i(getActivity().getPackageName(),requestOpinionInput.toString());

        Observable<Response<RequestOpinionOutput>> registerResponseObservable = registerService.requestOpinionForNewProduct(requestOpinionInput);

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
                      storeGroupInDB(groupName);
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

    private void storeGroupInDB(String groupName) {
        DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext());
        ContactGroup group = new ContactGroup();
        group.setGroupName(groupName);
        group.setContactArrayList(getSelectedContactsList());
        group.setSelect(false);
        db.addGroupWithContacts(group);
        cancelLoader();
    }
}
