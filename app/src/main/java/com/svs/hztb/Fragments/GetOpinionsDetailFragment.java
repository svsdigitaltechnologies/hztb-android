package com.svs.hztb.Fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.svs.hztb.Activities.HomeScreenActivity;
import com.svs.hztb.Adapters.GetDetailOpinionAdapter;
import com.svs.hztb.Adapters.GetOpinionAdapter;
import com.svs.hztb.Adapters.GetOpinionNonResponsiveAdapter;
import com.svs.hztb.Bean.OpinionData;
import com.svs.hztb.Bean.OpinionResponseData;
import com.svs.hztb.Bean.OpinionResponseInfo;
import com.svs.hztb.Bean.RefreshInput;
import com.svs.hztb.Database.AppSharedPreference;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class GetOpinionsDetailFragment extends Fragment {
    protected LoadingBar _loader;
    private ListView responseListView;
    private ListView notResponseListView;

    private Button respondedButton;
    private Button notrespondedButton;
    public GetOpinionsDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_opinions_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _loader=new LoadingBar(getActivity());
        initViews(view);


    }


    private void initViews(final View view) {
        final OpinionData opinionData = ((HomeScreenActivity)getActivity()).getProduct();

        TextView productName = (TextView)view.findViewById(R.id.textView2);
        TextView productID = (TextView)view.findViewById(R.id.textView3);
        TextView productDescription = (TextView)view.findViewById(R.id.product_description);
        TextView productPrice = (TextView)view.findViewById(R.id.product_price);
        ImageView productImage =(ImageView)view.findViewById(R.id.product_thumb_black);

        if (opinionData.getProduct().getImageUrl() != null){
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
            ImageLoader.getInstance().displayImage(opinionData.getProduct().getImageUrl(), productImage,options);
        }

        Button viewSelfie = (Button)view.findViewById(R.id.button_capture_selfie);
        if (opinionData.getSelfieUrl() == null){
            viewSelfie.setVisibility(View.INVISIBLE);
        }
        viewSelfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout opinionDetailLayout = (LinearLayout)view.findViewById(R.id.detail_opinion_layout);
                RelativeLayout selfieLayout = (RelativeLayout)view.findViewById(R.id.viewSelfie_layout);
                opinionDetailLayout.setVisibility(View.GONE);
                selfieLayout.setVisibility(View.VISIBLE);
                ImageView selfieImageview = (ImageView)view.findViewById(R.id.selfie_imageView);
                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
                ImageLoader.getInstance().displayImage(opinionData.getSelfieUrl(), selfieImageview,options);
            }
        });
        productName.setText(opinionData.getProductName());

        Button backButton = (Button)view.findViewById(R.id.button_back);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout opinionDetailLayout = (LinearLayout)view.findViewById(R.id.detail_opinion_layout);
                RelativeLayout selfieLayout = (RelativeLayout)view.findViewById(R.id.viewSelfie_layout);
                opinionDetailLayout.setVisibility(View.VISIBLE);
                selfieLayout.setVisibility(View.GONE);
            }
        });

        responseListView = (ListView)view.findViewById(R.id.listview_responded);
        notResponseListView = (ListView)view.findViewById(R.id.listview_not_responded);

        respondedButton  = (Button)view.findViewById(R.id.details_responded_button);
        respondedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                respondedButton.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorAccent));
                notrespondedButton.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.light_grey));
                responseListView.setVisibility(View.VISIBLE);
                notResponseListView.setVisibility(View.GONE);
            }
        });
        notrespondedButton  = (Button)view.findViewById(R.id.details_not_responded_button);
        notrespondedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notrespondedButton.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorAccent));
                respondedButton.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.light_grey));
                responseListView.setVisibility(View.GONE);
                notResponseListView.setVisibility(View.VISIBLE);
            }
        });
        ConnectionDetector c = new ConnectionDetector(getActivity().getApplicationContext());
        if(c.isConnectingToInternet()) {
            postDataToGetOpinionsResponses(opinionData);
        }else{
            Toast.makeText(getActivity().getApplicationContext(),"No Network",Toast.LENGTH_LONG).show();
        }
    }

    private void postDataToGetOpinionsResponses(OpinionData opinionData) {

        showLoader();
        OpinionService opinionService = new OpinionService();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        RefreshInput refreshInput = new RefreshInput();
        refreshInput.setOpinionId(opinionData.getOpinionId());
//        refreshInput.setOpinionId(7);
//        refreshInput.setLastUpdatedTime(dateFormat.format(date));
        refreshInput.setLastUpdatedTime("2015-01-01 01:01:01");
   //     String json = toJson(refreshInput);

        Observable<Response<OpinionResponseInfo>> refreshResponseObservable = opinionService.getOpinionsDetail(refreshInput);

        refreshResponseObservable.observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).subscribe(new Subscriber<Response<OpinionResponseInfo>>() {
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
            public void onNext(Response<OpinionResponseInfo> requestResponse) {

                if (requestResponse.isSuccessful()) {
                    List<OpinionResponseData> opinionResponseDataList =requestResponse.body().getOpinionResponseDataList();
                    List<Integer> opinionNonResponsiveId = requestResponse.body().getUnResponsiveUsers();
                    GetDetailOpinionAdapter responsiveAdapter = new GetDetailOpinionAdapter(getActivity().getApplicationContext(),opinionResponseDataList);
                    GetOpinionNonResponsiveAdapter nonResponsiveAdapter = new GetOpinionNonResponsiveAdapter(getActivity().getApplicationContext(),opinionNonResponsiveId);
                    responseListView.setAdapter(responsiveAdapter);
                    notResponseListView.setAdapter(nonResponsiveAdapter);
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
