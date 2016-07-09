package com.svs.hztb.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svs.hztb.Adapters.RetriveGroupsAdapter;
import com.svs.hztb.Bean.GroupDetail;
import com.svs.hztb.Bean.Product;
import com.svs.hztb.Bean.RequestOpinionInput;
import com.svs.hztb.Bean.RequestOpinionOutput;
import com.svs.hztb.Bean.Status;
import com.svs.hztb.Bean.UserID;
import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.R;
import com.svs.hztb.RealmDatabase.GroupDetailRealm;
import com.svs.hztb.RealmDatabase.RealmDatabase;
import com.svs.hztb.RealmDatabase.RealmInt;
import com.svs.hztb.RestService.ErrorStatus;
import com.svs.hztb.RestService.OpinionService;
import com.svs.hztb.RestService.ServiceGenerator;
import com.svs.hztb.Utils.LoadingBar;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.realm.RealmList;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewRequestFragment extends Fragment {

    private ListView groupsList;
    protected LoadingBar _loader;
    private ArrayList<GroupDetail> groupList;
    private Button reguestOpinionButton;
    private RetriveGroupsAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_request, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        groupsList = (ListView) view.findViewById(R.id.listview_groups);
        reguestOpinionButton =(Button)view.findViewById(R.id.button_request_opinion);
        reguestOpinionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Integer> selectedGroup = new ArrayList<Integer>();
                Iterator<GroupDetail> itertor = groupList.iterator();
                while (itertor.hasNext()){
                    GroupDetail group = itertor.next();
                    if (group.isSelect() == true){
                        selectedGroup.add(group.getGroupId());
                    }
                }
                if (selectedGroup.size() > 0){
                    postDataForNewRequest(selectedGroup);
                }
            }
        });

        _loader=new LoadingBar(getActivity());
        showLoader();

        postDataToGetGroups();

    }
    private void postDataForNewRequest(ArrayList<Integer> selectedGroup) {
        showLoader();

        OpinionService opinionService = new OpinionService();
        RequestOpinionInput requestOpinionInput = new RequestOpinionInput();
        requestOpinionInput.setRequesterUserId(Integer.valueOf(new AppSharedPreference().getUserID(getActivity().getApplicationContext())));
        Product product = new Product();
        product.setName("Test606163");
        product.setShortDesc("Awesome");
        product.setLongDesc("brilliant");
        product.setImageUrl("c:/ada/asdasd");
        product.setPrice(22.0);
        requestOpinionInput.setProduct(product);
        requestOpinionInput.setRequestedGroupIds(selectedGroup);
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
                        clearSelectionList();
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

    private void clearSelectionList() {
        Iterator<GroupDetail> itertor = groupList.iterator();
        while (itertor.hasNext()){
            GroupDetail group = itertor.next();
            if (group.isSelect() == true){
                group.setSelect(false);
            }
        }
        adapter.notifyDataSetChanged();
    }


    private void postDataToGetGroups() {
        groupList = new ArrayList<>();
        OpinionService opinionService = new OpinionService();
        UserID userId = new UserID();
        AppSharedPreference sherdPref = new AppSharedPreference();
        int id = Integer.valueOf(sherdPref.getUserID(getActivity().getApplicationContext()));
        userId.setUserID(id);
        Observable<Response<List<GroupDetail>>> getGroupObservable = opinionService.getGroups(userId);

        getGroupObservable.observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).subscribe(new Subscriber<Response<List<GroupDetail>>>() {
            @Override
            public void onCompleted() {
                cancelLoader();
            }

            @Override
            public void onError(Throwable e) {
                cancelLoader();
                Log.d("Error ",e.toString());
            }

            @Override
            public void onNext(Response<List<GroupDetail>> groupResponse) {

                if (groupResponse.isSuccessful()) {
                    if (groupResponse.body().size() > 0) {

                        ArrayList<GroupDetail> detailList = new ArrayList<GroupDetail>();

                        detailList = (ArrayList<GroupDetail>) groupResponse.body();
                        groupList.addAll(detailList);
                        GroupDetail group = new GroupDetail();
                        group.setGroupName("Select From Contacts");
                        groupList.add(group);
                        adapter = new RetriveGroupsAdapter(getActivity().getApplicationContext(), groupList);
                        groupsList.setAdapter(adapter);
                        groupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                if (groupList.size() - 1 == i) {
                                    ContactsFragment fragment = new ContactsFragment();
                                    String backStateName = fragment.getClass().getName();
                                    FragmentManager fragmentManager = getFragmentManager();
                                    boolean fragmentPopped = fragmentManager
                                            .popBackStackImmediate(backStateName, 0);
                                    if (!fragmentPopped) {
                                        FragmentTransaction ftx = fragmentManager.beginTransaction();
                                        ftx.replace(R.id.fragment, fragment);
                                        ftx.addToBackStack(backStateName);
                                        ftx.commit();
                                    }
                                }
                                else {
                                    if (!groupList.get(i).isSelect()) {
                                        groupList.get(i).setSelect(true);
                                    }else groupList.get(i).setSelect(false);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                        adapter.notifyDataSetChanged();
                        addAllTheListToDatabase(detailList);
                    }
                }
                else Toast.makeText(getActivity().getApplicationContext(),"No Options Available",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void addAllTheListToDatabase(ArrayList<GroupDetail> groupDetailList){
        Iterator<GroupDetail> iterator = groupDetailList.iterator();
        RealmList<GroupDetailRealm> groupDetailRealmList = new RealmList<>();
        while (iterator.hasNext()){
            GroupDetail groupDetail = iterator.next();
            GroupDetailRealm groupDetailRealm = new GroupDetailRealm();
            groupDetailRealm.setGroupName(groupDetail.getGroupName());
            groupDetailRealm.setGroupId(groupDetail.getGroupId());
            RealmList<RealmInt> intRealmList = new RealmList<>();
            Iterator<Integer> integerIterator = groupDetail.getGroupMembers().iterator();
            while (integerIterator.hasNext()){
                int i = integerIterator.next();
                RealmInt realmInt = new RealmInt();
                realmInt.setVal(i);
                intRealmList.add(realmInt);
            }
            groupDetailRealm.setGroupMembers(intRealmList);
            groupDetailRealmList.add(groupDetailRealm);
        }

        RealmDatabase realmDatabase = new RealmDatabase();
        realmDatabase.addGroupsListToDatabase(groupDetailRealmList);
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
