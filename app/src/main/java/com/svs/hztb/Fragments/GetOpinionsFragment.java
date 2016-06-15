package com.svs.hztb.Fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.svs.hztb.RestService.ErrorStatus;
import com.svs.hztb.RestService.OpinionService;
import com.svs.hztb.RestService.ServiceGenerator;
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
        postDataToGetOpinions();

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

        RefreshInput refreshInput = new RefreshInput();
//        refreshInput.setUserId(Integer.valueOf(new AppSharedPreference().getUserID(getActivity().getApplicationContext())));
        refreshInput.setUserId(1);
        refreshInput.setLastUpdatedTime("2015-01-01 01:01:01");
        String json = toJson(refreshInput);
        Log.d("Json Object"+json,"");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));

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
                    opinionDataArrayList = (ArrayList<OpinionData>) requestResponse.body();
                    adapter = new GetOpinionAdapter(getActivity().getApplicationContext(),opinionDataArrayList);
                    listview_getOpinions.setAdapter(adapter);
                }
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
}
