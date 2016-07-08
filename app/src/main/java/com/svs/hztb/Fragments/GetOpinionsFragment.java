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
import com.svs.hztb.Bean.Product;
import com.svs.hztb.Bean.RefreshInput;
import com.svs.hztb.Bean.RefreshOutput;
import com.svs.hztb.Bean.RequestOpinionInput;
import com.svs.hztb.Bean.RequestOpinionOutput;
import com.svs.hztb.Bean.Status;
import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.R;
import com.svs.hztb.RealmDatabase.RealmOpinionData;
import com.svs.hztb.RealmDatabase.RealmOpinionDatabase;
import com.svs.hztb.RealmDatabase.RealmProduct;
import com.svs.hztb.RealmDatabase.RealmResponseCount;
import com.svs.hztb.RestService.ErrorStatus;
import com.svs.hztb.RestService.OpinionService;
import com.svs.hztb.RestService.ServiceGenerator;
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

import io.realm.RealmList;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GetOpinionsFragment extends android.app.Fragment {

    private ArrayList<OpinionData> opinionDataArrayList;
    protected LoadingBar _loader;
    private ListView listview_getOpinions;
    private GetOpinionAdapter adapter;
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
        ConnectionDetector c = new ConnectionDetector(getActivity().getApplicationContext());
        if(c.isConnectingToInternet()) {

            postDataToGetOpinions();
        }else{
            Toast.makeText(getActivity().getApplicationContext(),"No Network",Toast.LENGTH_LONG).show();
        }

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

    private void postDataToGetOpinions() {
        showLoader();
        OpinionService opinionService = new OpinionService();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        RefreshInput refreshInput = new RefreshInput();
   //     refreshInput.setUserId(Integer.valueOf(new AppSharedPreference().getUserID(getActivity().getApplicationContext())));
        refreshInput.setUserId(1);
  //      refreshInput.setLastUpdatedTime(dateFormat.format(date));
        refreshInput.setLastUpdatedTime("2015-01-01 01:01:01");
 //       String json = toJson(refreshInput);

        Observable<Response<List<OpinionData>>> refreshResponseObservable = opinionService.getOpinions(refreshInput);

        refreshResponseObservable.observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).subscribe(new Subscriber<Response<List<OpinionData>>>() {
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
            public void onNext(Response<List<OpinionData>> requestResponse) {

                if (requestResponse.isSuccessful()) {
                    if (requestResponse.body().size() > 0) {
                        opinionDataArrayList = (ArrayList<OpinionData>) requestResponse.body();
                        adapter = new GetOpinionAdapter(getActivity().getApplicationContext(), opinionDataArrayList);
                        storeOpinionDataInDatabase(opinionDataArrayList);
                        listview_getOpinions.setAdapter(adapter);
                        listview_getOpinions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                ((HomeScreenActivity)getActivity()).setProduct(opinionDataArrayList.get(position));
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
            realmOpinionData.setProduct(realmProduct);

            opinionDataList.add(realmOpinionData);
        }
        RealmOpinionDatabase database = new RealmOpinionDatabase();
        database.addRealmOpinionData(opinionDataList);
        database.getAllOpinions();

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
