package com.svs.hztb.Fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svs.hztb.Activities.HomeScreenActivity;
import com.svs.hztb.Adapters.GetDetailOpinionAdapter;
import com.svs.hztb.Adapters.GetOpinionNonResponsiveAdapter;
import com.svs.hztb.Adapters.OpinionDetailGivenAdapter;
import com.svs.hztb.Bean.GivenPendingData;
import com.svs.hztb.Bean.OpinionCountData;
import com.svs.hztb.Bean.OpinionResponseData;
import com.svs.hztb.Bean.OpinionResponseInfo;
import com.svs.hztb.Bean.RefreshInput;
import com.svs.hztb.Bean.ResponseGivenPendingInfo;
import com.svs.hztb.R;
import com.svs.hztb.RestService.OpinionService;
import com.svs.hztb.Utils.ConnectionDetector;
import com.svs.hztb.Utils.LoadingBar;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class OpinionGivenDetailFragment extends Fragment {
    protected LoadingBar _loader;
    private ListView opinionGivenListView;
    private ListView opinionPendingListView;

    private Button opinionGiveButton;
    private Button opinionPendingButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_opinion_given_detail, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _loader=new LoadingBar(getActivity());
        initViews(view);


    }

    private void initViews(View view) {

        OpinionCountData opinionCountData = ((HomeScreenActivity)getActivity()).getOpinionCountData();

        TextView userName = (TextView)view.findViewById(R.id.textview_usernameId);
        userName.setText(String.valueOf("User ID :"+opinionCountData.getUserId()));
        opinionGivenListView = (ListView)view.findViewById(R.id.opinion_given_listview);
        opinionPendingListView = (ListView)view.findViewById(R.id.opinion_pending_listview);

        opinionGiveButton  = (Button)view.findViewById(R.id.opinion_given_button);
        opinionGiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opinionGiveButton.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorAccent));
                opinionPendingButton.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.light_grey));
                opinionGivenListView.setVisibility(View.VISIBLE);
                opinionPendingListView.setVisibility(View.GONE);
            }
        });
        opinionPendingButton  = (Button)view.findViewById(R.id.opinion_pending_button);
        opinionPendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opinionPendingButton.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorAccent));
                opinionGiveButton.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.light_grey));
                opinionGivenListView.setVisibility(View.GONE);
                opinionPendingListView.setVisibility(View.VISIBLE);
            }
        });
        ConnectionDetector c = new ConnectionDetector(getActivity().getApplicationContext());
        if(c.isConnectingToInternet()) {
            postDataToGetGivenAndPendingOpinionsResponses(opinionCountData);
        }else{
            Toast.makeText(getActivity().getApplicationContext(),"No Network",Toast.LENGTH_LONG).show();
        }
    }

    private void postDataToGetGivenAndPendingOpinionsResponses(OpinionCountData opinionCountData) {
        showLoader();
        OpinionService opinionService = new OpinionService();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        RefreshInput refreshInput = new RefreshInput();
//        refreshInput.setOpinionId(opinionData.getOpinionId());
        refreshInput.setUserId(1);
        refreshInput.setResponderUserId("2");
//        refreshInput.setLastUpdatedTime(dateFormat.format(date));
        refreshInput.setLastUpdatedTime("2015-01-01 01:01:01");
            String json = toJson(refreshInput);

        Observable<Response<ResponseGivenPendingInfo>> refreshResponseObservable = opinionService.opinionsGivenDetail(refreshInput);

        refreshResponseObservable.observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).subscribe(new Subscriber<Response<ResponseGivenPendingInfo>>() {
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
            public void onNext(Response<ResponseGivenPendingInfo> requestResponse) {

                if (requestResponse.isSuccessful()) {
                    List<GivenPendingData> opinionGivenDataList =requestResponse.body().getGivenData();
                    final List<GivenPendingData> opinionPendingDataList = requestResponse.body().getPendingData();
                    OpinionDetailGivenAdapter adapter = new OpinionDetailGivenAdapter(getActivity().getApplicationContext(),opinionGivenDataList);
                    OpinionDetailGivenAdapter pendingAdapter = new OpinionDetailGivenAdapter(getActivity().getApplicationContext(),opinionPendingDataList);
                    opinionGivenListView.setAdapter(adapter);
                    opinionPendingListView.setAdapter(pendingAdapter);
                    opinionPendingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ((HomeScreenActivity)getActivity()).setGivenPendingData(opinionPendingDataList.get(position));

                            OpinionInputDetailFragment opinioninputFragment = new OpinionInputDetailFragment();
                            String backStateName = opinioninputFragment.getClass().getName();
                            FragmentManager fragmentManager = getFragmentManager();
                            boolean fragmentPopped = fragmentManager
                                    .popBackStackImmediate(backStateName, 0);
                            if (!fragmentPopped) {
                                FragmentTransaction ftx = fragmentManager.beginTransaction();
                                ftx.replace(R.id.fragment, opinioninputFragment);
                                ftx.addToBackStack(backStateName);
                                ftx.commit();
                            }
                        }
                    });
                    opinionGivenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ((HomeScreenActivity)getActivity()).setGivenPendingData(opinionPendingDataList.get(position));

                            OpinionInputDetailFragment opinioninputFragment = new OpinionInputDetailFragment();
                            String backStateName = opinioninputFragment.getClass().getName();
                            FragmentManager fragmentManager = getFragmentManager();
                            boolean fragmentPopped = fragmentManager
                                    .popBackStackImmediate(backStateName, 0);
                            if (!fragmentPopped) {
                                FragmentTransaction ftx = fragmentManager.beginTransaction();
                                ftx.replace(R.id.fragment, opinioninputFragment);
                                ftx.addToBackStack(backStateName);
                                ftx.commit();
                            }
                        }
                    });
                }
            }
        });


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
