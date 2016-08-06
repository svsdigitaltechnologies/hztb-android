package com.svs.hztb.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svs.hztb.Activities.HomeScreenActivity;
import com.svs.hztb.Adapters.GetOpinionAdapter;
import com.svs.hztb.Adapters.OpinionGivenAdapter;
import com.svs.hztb.Bean.OpinionCountData;
import com.svs.hztb.Bean.OpinionData;
import com.svs.hztb.Bean.RefreshInput;
import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.Interfaces.IRealmDataStoredCallBack;
import com.svs.hztb.R;
import com.svs.hztb.RealmDatabase.RealmDatabase;
import com.svs.hztb.RealmDatabase.RealmOpinionCountData;
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
import java.util.TimeZone;

import io.realm.RealmList;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OpinionGivenFragment extends Fragment implements IRealmDataStoredCallBack{

    private ArrayList<OpinionCountData> opinionGivenDataArrayList;
    protected LoadingBar _loader;
    private ListView listviewOpinionsGiven;
    private OpinionGivenAdapter adapter;
    private RealmDatabase database;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_opinion_given, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _loader=new LoadingBar(getActivity());
        listviewOpinionsGiven = (ListView)view.findViewById(R.id.listview_OpinionGiven);
        ConnectionDetector c = new ConnectionDetector(getActivity().getApplicationContext());
        database = new RealmDatabase(this);
        opinionGivenDataArrayList = database.getAllOpinionsGiven();

        if(c.isConnectingToInternet()) {
            if (opinionGivenDataArrayList.size() > 0)
            {
                configureListViewAndAdapter();
                postDataToGetOpinionsGiven(true);
            }else {
                postDataToGetOpinionsGiven(false);
            }
        }else{
            Toast.makeText(getActivity().getApplicationContext(),"No Network",Toast.LENGTH_LONG).show();
        }

    }

    private void postDataToGetOpinionsGiven(final boolean isBackground) {
        if (!isBackground)
        showLoader();

        OpinionService opinionService = new OpinionService();
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Date date = new Date();
        dateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));

        RefreshInput refreshInput = new RefreshInput();
        refreshInput.setUserId(Integer.valueOf(new AppSharedPreference().getUserID(getActivity().getApplicationContext())));

        String lastUpdatedDate = new AppSharedPreference().getLastOpinionGivenDate(getActivity().getApplicationContext());
        if (lastUpdatedDate != null){
            refreshInput.setLastUpdatedTime(lastUpdatedDate);
        }else {
            refreshInput.setLastUpdatedTime("2016-07-29 01:01:01");
        }
      //  refreshInput.setLastUpdatedTime("2015-01-01 01:01:01");
      //  String json = toJson(refreshInput);

        Observable<Response<List<OpinionCountData>>> refreshResponseObservable = opinionService.opinionGiven(refreshInput);

        refreshResponseObservable.observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).subscribe(new Subscriber<Response<List<OpinionCountData>>>() {
            @Override
            public void onCompleted() {
                if (!isBackground)
                    cancelLoader();
            }

            @Override
            public void onError(Throwable e) {
                if (!isBackground)
                cancelLoader();
                Log.d("Error ",e.toString());
            }

            @Override
            public void onNext(Response<List<OpinionCountData>> requestResponse) {

                if (requestResponse.isSuccessful()) {
                    if (requestResponse.body().size() > 0) {

                        if (requestResponse.body().size() > 0) {
                            storeOpinionCountDataToDB(requestResponse.body());
                            new  AppSharedPreference().storeLastOpinionGivenDate(dateFormat.format(date),getActivity().getApplicationContext());
                        }
                    }
                }
                else Toast.makeText(getActivity().getApplicationContext(),"No Options Available",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        database.removeIDataStoredCallBack();
    }

    private void configureListViewAndAdapter(){
        adapter = new OpinionGivenAdapter(getActivity().getApplicationContext(), opinionGivenDataArrayList);
        listviewOpinionsGiven.setAdapter(adapter);
        listviewOpinionsGiven.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ((HomeScreenActivity) getActivity()).setOpinionData(opinionGivenDataArrayList.get(position));

                OpinionGivenDetailFragment opinionDetailsFrg = new OpinionGivenDetailFragment();
                String backStateName = opinionDetailsFrg.getClass().getName();
                FragmentManager fragmentManager = getFragmentManager();
                boolean fragmentPopped = fragmentManager
                        .popBackStackImmediate(backStateName, 0);
                if (!fragmentPopped) {
                    FragmentTransaction ftx = fragmentManager.beginTransaction();
                    Bundle bundle = new Bundle();
                    int c = opinionGivenDataArrayList.get(position).getUserId();

                    bundle.putInt("responderUserId", opinionGivenDataArrayList.get(position).getUserId());
                    opinionDetailsFrg.setArguments(bundle);
                    ftx.replace(R.id.fragment, opinionDetailsFrg);
                    ftx.addToBackStack(backStateName);
                    ftx.commit();
                }
            }
        });
    }

    private void storeOpinionCountDataToDB(List<OpinionCountData> opinionList) {
        RealmList<RealmOpinionCountData> list = new RealmList<>();
        Iterator<OpinionCountData> opinionIterator = opinionList.iterator();
        while (opinionIterator.hasNext()){
            OpinionCountData opinionCountObj = opinionIterator.next();
            RealmOpinionCountData realmCountObj = new RealmOpinionCountData();
            realmCountObj.setUserId(opinionCountObj.getUserId());
            realmCountObj.setGivenCount(opinionCountObj.getGivenCount());
            realmCountObj.setPendingCount(opinionCountObj.getPendingCount());
            list.add(realmCountObj);
        }
        database.addRealmOpinionCountData(list);
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

    @Override
    public void dataSuccessfullyStore(Boolean successful) {
        opinionGivenDataArrayList = database.getAllOpinionsGiven();
        configureListViewAndAdapter();
    }
}
