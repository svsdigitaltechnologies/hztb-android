package com.svs.hztb.Fragments;

import android.app.Fragment;

import android.os.Bundle;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svs.hztb.Adapters.ContactsAdapter;
import com.svs.hztb.Bean.Contact;
import com.svs.hztb.Bean.GroupDetail;
import com.svs.hztb.Bean.GroupOutput;
import com.svs.hztb.Bean.OpinionData;
import com.svs.hztb.Bean.Product;
import com.svs.hztb.Bean.RemoveGroup;
import com.svs.hztb.Bean.RequestOpinionInput;
import com.svs.hztb.Bean.RequestOpinionOutput;
import com.svs.hztb.Bean.Status;
import com.svs.hztb.Bean.UserData;
import com.svs.hztb.CustomViews.WalkWayButton;
import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.Interfaces.ContactsSyncCompleted;
import com.svs.hztb.R;
import com.svs.hztb.RealmDatabase.GroupDetailRealm;
import com.svs.hztb.RealmDatabase.RealmDatabase;
import com.svs.hztb.RealmDatabase.RealmUserData;
import com.svs.hztb.RealmDatabase.RealmUserProfileResponse;
import com.svs.hztb.RestService.ErrorStatus;
import com.svs.hztb.RestService.OpinionService;
import com.svs.hztb.RestService.ServiceGenerator;
import com.svs.hztb.Utils.ContactsSync;
import com.svs.hztb.Utils.LoadingBar;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class EditOrAddGroupFragment extends Fragment implements ContactsSyncCompleted {
    private ArrayList<Contact> contactList;
    private ListView contactsListView;
    private ContactsAdapter adapter;
    EditText contactSearch,groupName;
    private Button doneButton;

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2001;
    protected LoadingBar _loader;
    private boolean isGroupAdd ;
    private int groupId;
    private RealmDatabase database;
    private  GroupDetailRealm group;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        isGroupAdd = getArguments().getBoolean("isAdd");
        groupId = getArguments().getInt("groupId");

        return inflater.inflate(R.layout.fragment_edit_or_add_group, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView groupText = (TextView)view.findViewById(R.id.textView_group_header);
        Button deleteButton = (Button)view.findViewById(R.id.button_group_delete);
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
        groupName = (EditText)view.findViewById(R.id.editText_group);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDoneButtonClicked();
            }
        });

        if (isGroupAdd){
            deleteButton.setVisibility(View.GONE);
        }else {
            database = new RealmDatabase();
            groupText.setText("Edit Group Name");
            Realm realm = Realm.getDefaultInstance();
            group =realm.where(GroupDetailRealm.class).equalTo("groupId", groupId).findFirst();
            groupName.setText(group.getGroupName());
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postDataToDeleteGroup();
                }
            });
        }

        initviews();
    }

    private void postDataToDeleteGroup() {
        showLoader();

        OpinionService opinionService = new OpinionService();
        RemoveGroup removeGroup = new RemoveGroup();
        removeGroup.setUserId(Integer.valueOf(new AppSharedPreference().getUserID(getActivity().getApplicationContext())));
        removeGroup.setGroupId(groupId);
        Observable<Response<GroupOutput>> registerResponseObservable = opinionService.requestToDeleteGroup(removeGroup);

        registerResponseObservable.observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).subscribe(new Subscriber<Response<GroupOutput>>() {
            @Override
            public void onCompleted() {
                cancelLoader();
            }

            @Override
            public void onError(Throwable e) {
                cancelLoader();
            }

            @Override
            public void onNext(Response<GroupOutput> requestResponse) {

                if (requestResponse.isSuccessful()) {

                    if (requestResponse.body().getStatus() == Status.SUCCESS){
                        Toast.makeText(getActivity().getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.where(GroupDetailRealm.class).equalTo("groupId", groupId).findAll().deleteAllFromRealm();
                        realm.commitTransaction();
                        getFragmentManager().popBackStack();
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


    private void syncContacts() {
        ContactsSync contactsSync = new ContactsSync(this,getActivity());
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
                EditOrAddGroupFragment.this.adapter.filter(""+cs);
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

        if (!isGroupAdd)
        {
            setSelectedContacts();
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

    private void setSelectedContacts() {
        if (group != null){
            Iterator<RealmUserData> realmIterator = group.getUserDataList().iterator();
            while (realmIterator.hasNext()){
                RealmUserData userData = realmIterator.next();
                Iterator<Contact> contactIterator = contactList.iterator();
                while (contactIterator.hasNext()){
                    Contact contact = contactIterator.next();
                    if (Integer.valueOf(contact.getUserId()) == userData.getUserId()){
                        contact.setSelected(true);
                    }
                }
            }
        }
    }


    public void onDoneButtonClicked(){
        ArrayList<Integer> contactsSelected = getSelectedContactsList();
        if (contactsSelected.size() > 0) {
            if (groupName.getText().length()>0){
                if (isGroupAdd) {
                    postDataToCreateNewGroup();
                }else
                {
                    postDataToEditGroup();
                }
            }
            else {
                Toast.makeText(getActivity().getApplicationContext(),"Plese Add Group Name",Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getActivity().getApplicationContext(),"Please Select Contact",Toast.LENGTH_LONG).show();
        }
    }

    private void postDataToEditGroup() {
    }

    private void postDataToCreateNewGroup() {
        showLoader();

        OpinionService opinionService = new OpinionService();
        GroupDetail groupDetail = new GroupDetail();
        groupDetail.setUserId(Integer.valueOf(new AppSharedPreference().getUserID(getActivity().getApplicationContext())));
        groupDetail.setGroupName(groupName.getText().toString());
        groupDetail.setAddMembers(getSelectedContactsList());

        String d = toJson(groupDetail);
        Observable<Response<GroupOutput>> registerResponseObservable = opinionService.requestNewGroup(groupDetail);

        registerResponseObservable.observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).subscribe(new Subscriber<Response<GroupOutput>>() {
            @Override
            public void onCompleted() {
                cancelLoader();
            }

            @Override
            public void onError(Throwable e) {
                cancelLoader();
            }

            @Override
            public void onNext(Response<GroupOutput> requestResponse) {

                if (requestResponse.isSuccessful()) {

                    if (requestResponse.body().getStatus() == Status.SUCCESS){
                        Toast.makeText(getActivity().getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getActivity().getApplicationContext(),"UNSUCCESSFUL",Toast.LENGTH_LONG).show();
                    }
                    getFragmentManager().popBackStack();
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

    private ArrayList<Integer> getSelectedContactsList() {
        ArrayList<Integer> selectedList = new ArrayList<>();

        Iterator<Contact> iterator = contactList.iterator();
        while (iterator.hasNext()){
            Contact contactItem = iterator.next();
            if (contactItem.isSelected() == true){
                selectedList.add(Integer.valueOf(contactItem.getUserId()));
            }
        }
        return selectedList;
    }



    @Override
    public void onContactsSyncCompleted() {
        loadListViewDataFromDatabase();
        Log.d("SyncCompleted","Syncing");
    }


    public static String toJson(Object object) {
        String jsonString = "";
        ObjectMapper mapper = new ObjectMapper();
        Writer strWriter = new StringWriter();
        try {
            mapper.writeValue(strWriter, object);
            jsonString = strWriter.toString();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonString;
    }

}
