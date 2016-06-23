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
import com.svs.hztb.R;
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
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OpinionGivenFragment extends Fragment {

    private ArrayList<OpinionCountData> opinionGivenDataArrayList;
    protected LoadingBar _loader;
    private ListView listviewOpinionsGiven;
    private OpinionGivenAdapter adapter;

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
        if(c.isConnectingToInternet()) {

           postDataToGetOpinionsGiven();
        }else{
            Toast.makeText(getActivity().getApplicationContext(),"No Network",Toast.LENGTH_LONG).show();
        }

    }

    private void postDataToGetOpinionsGiven() {
        showLoader();
        OpinionService opinionService = new OpinionService();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        RefreshInput refreshInput = new RefreshInput();
        //     refreshInput.setUserId(Integer.valueOf(new AppSharedPreference().getUserID(getActivity().getApplicationContext())));
        refreshInput.setUserId(1);
        //      refreshInput.setLastUpdatedTime(dateFormat.format(date));
        refreshInput.setLastUpdatedTime("2015-01-01 01:01:01");
               String json = toJson(refreshInput);

        Observable<Response<List<OpinionCountData>>> refreshResponseObservable = opinionService.opinionGiven(refreshInput);

        refreshResponseObservable.observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).subscribe(new Subscriber<Response<List<OpinionCountData>>>() {
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
            public void onNext(Response<List<OpinionCountData>> requestResponse) {

                if (requestResponse.isSuccessful()) {
                    if (requestResponse.body().size() > 0) {
                        opinionGivenDataArrayList = (ArrayList<OpinionCountData>) requestResponse.body();
                        adapter = new OpinionGivenAdapter(getActivity().getApplicationContext(), opinionGivenDataArrayList);
                        listviewOpinionsGiven.setAdapter(adapter);
                        listviewOpinionsGiven.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                ((HomeScreenActivity)getActivity()).setOpinionData(opinionGivenDataArrayList.get(position));

                                OpinionGivenDetailFragment opinionDetailsFrg = new OpinionGivenDetailFragment();
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

}
