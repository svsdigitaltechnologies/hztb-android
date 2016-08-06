package com.svs.hztb.Fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svs.hztb.Activities.HomeScreenActivity;
import com.svs.hztb.Adapters.GetOpinionAdapter;
import com.svs.hztb.Bean.OpinionData;
import com.svs.hztb.Bean.RefreshInput;
import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.Interfaces.IRealmDataStoredCallBack;
import com.svs.hztb.R;
import com.svs.hztb.RealmDatabase.RealmDatabase;
import com.svs.hztb.RealmDatabase.RealmOpinionData;
import com.svs.hztb.RealmDatabase.RealmProduct;
import com.svs.hztb.RealmDatabase.RealmResponseCount;
import com.svs.hztb.RestService.OpinionService;
import com.svs.hztb.Utils.ConnectionDetector;
import com.svs.hztb.Utils.LoadingBar;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import io.realm.RealmList;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GetOpinionsFragment extends android.app.Fragment implements IRealmDataStoredCallBack{

    private ArrayList<OpinionData> opinionDataArrayList;
    protected LoadingBar _loader;
    private ListView listview_getOpinions;
    private GetOpinionAdapter adapter;
    private RealmDatabase database;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_opinions, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _loader=new LoadingBar(getActivity());
        listview_getOpinions = (ListView)view.findViewById(R.id.listview_getOpinions);

        database = new RealmDatabase(this);
        opinionDataArrayList = database.getAllOpinions();
        if (opinionDataArrayList.size() == 0){
            checkForNewOpinions();
            configureListviewAndAdapter();
        }else {
            postDataToGetOpinions(true);
            configureListviewAndAdapter();
        }



    }

    private void configureListviewAndAdapter(){
        adapter = new GetOpinionAdapter(getActivity().getApplicationContext(), opinionDataArrayList);
        listview_getOpinions.setAdapter(adapter);
        listview_getOpinions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((HomeScreenActivity) getActivity()).setProduct(opinionDataArrayList.get(position));
                GetOpinionsDetailFragment opinionDetailsFrg = new GetOpinionsDetailFragment();
                String backStateName = opinionDetailsFrg.getClass().getName();
                FragmentManager fragmentManager = getFragmentManager();
                boolean fragmentPopped = fragmentManager
                        .popBackStackImmediate(backStateName, 0);
                if (!fragmentPopped) {
                    FragmentTransaction ftx = fragmentManager.beginTransaction();
                    ftx.replace(R.id.fragment, opinionDetailsFrg);
                    ftx.addToBackStack(backStateName);
                    ftx.commit();
                }
            }
        });
    }

    public void checkForNewOpinions(){
        ConnectionDetector c = new ConnectionDetector(getActivity().getApplicationContext());
        if(c.isConnectingToInternet()) {
            postDataToGetOpinions(false);
        }else{
            Toast.makeText(getActivity().getApplicationContext(),"No Network",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        database.removeIDataStoredCallBack();

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

    private void postDataToGetOpinions(final boolean isBackgroundProcess) {
        if (!isBackgroundProcess)
        showLoader();

        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Date date = new Date();
        dateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));


        OpinionService opinionService = new OpinionService();


        RefreshInput refreshInput = new RefreshInput();
        refreshInput.setUserId(Integer.valueOf(new AppSharedPreference().getUserID(getActivity().getApplicationContext())));
        String lastUpdatedDate = new AppSharedPreference().getLastOpinionRecievedDate(getActivity().getApplicationContext());
        if (lastUpdatedDate != null){
            refreshInput.setLastUpdatedTime(lastUpdatedDate);
        }else {
            refreshInput.setLastUpdatedTime("2016-08-01 00:00:00");
        }

        refreshInput.setLastUpdatedTime("2016-08-01 00:00:00");


        Observable<Response<List<OpinionData>>> refreshResponseObservable = opinionService.getOpinions(refreshInput);
        refreshResponseObservable.observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).subscribe(new Subscriber<Response<List<OpinionData>>>() {
            @Override
            public void onCompleted() {
                if (!isBackgroundProcess)
                cancelLoader();
            }

            @Override
            public void onError(Throwable e) {
                if (!isBackgroundProcess)
                cancelLoader();

                Log.d("Error ",e.toString());
            }

            @Override
            public void onNext(Response<List<OpinionData>> requestResponse) {

                if (requestResponse.isSuccessful()) {
                    if (requestResponse.body().size() > 0) {
                        storeOpinionDataInDatabase((ArrayList<OpinionData>) requestResponse.body());
                        new  AppSharedPreference().storeLastOpinionRecievedDate(dateFormat.format(date),getActivity().getApplicationContext());
                    }
                }
                else Toast.makeText(getActivity().getApplicationContext(),"No Options Available",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void storeOpinionDataInDatabase(ArrayList<OpinionData> opinionDataArrayList) {
        RealmList<RealmOpinionData> opinionDataList = new RealmList<>();
        Iterator<OpinionData> iterator = opinionDataArrayList.iterator();
        while (iterator.hasNext()){
            OpinionData opinionData = iterator.next();
            RealmOpinionData realmOpinionData = new RealmOpinionData();
            realmOpinionData.setOpinionId(opinionData.getOpinionId());
            realmOpinionData.setProductName(opinionData.getProductName());
            realmOpinionData.setRequestedGroupId(opinionData.getRequestedGroupId());
            RealmList<RealmResponseCount> responCountList = new RealmList<>();
            for (Map.Entry<String, Integer> entry : opinionData.getResponseCounts().entrySet())
            {
                RealmResponseCount responseCount = new RealmResponseCount();
                responseCount.setResponseType(entry.getKey());
                responseCount.setResponseCount(entry.getValue());
                responCountList.add(responseCount);
                System.out.println(entry.getKey() + "/" + entry.getValue());
            }
            realmOpinionData.setResponseCountList(responCountList);
            RealmProduct realmProduct = new RealmProduct();
            realmProduct.setName(opinionData.getProduct().getName());
            realmProduct.setImageUrl(opinionData.getProduct().getImageUrl());
            realmProduct.setLongDesc(opinionData.getProduct().getLongDesc());
            realmProduct.setPrice(opinionData.getProduct().getPrice());
            realmProduct.setShortDesc(opinionData.getProduct().getShortDesc());
            realmOpinionData.setSelfieUrl(opinionData.getSelfieUrl());
            realmOpinionData.setProduct(realmProduct);

            opinionDataList.add(realmOpinionData);
        }
        database.addRealmOpinionData(opinionDataList);
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

    @Override
    public void dataSuccessfullyStore(Boolean successful) {
        if (successful){
            opinionDataArrayList = database.getAllOpinions();
            configureListviewAndAdapter();
        }
    }
}
