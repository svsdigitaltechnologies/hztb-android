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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svs.hztb.Activities.HomeScreenActivity;
import com.svs.hztb.Adapters.OpinionGivenAdapter;
import com.svs.hztb.Bean.GivenPendingData;
import com.svs.hztb.Bean.OpinionCountData;
import com.svs.hztb.Bean.OpinionResponseInput;
import com.svs.hztb.Bean.OpinionResponseOutput;
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
 * Created by VenuNalla on 6/24/16.
 */
public class OpinionInputDetailFragment extends Fragment {

    private ArrayList<OpinionCountData> opinionGivenDataArrayList;
    protected LoadingBar _loader;
    TextView productName;
    TextView productID;
    TextView productDescription;
    TextView productPrice;
    Button buttonOk;
    Button buttonSingleOk;
    Button buttonDown;
    Button buttonMayBe;
    Button viewSelf;
    Button sendOpinion;
    EditText responseText;
    String responseCode;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.opinion_detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _loader=new LoadingBar(getActivity());
        initViews(view);


    }

    private void initViews(View convertView) {
        GivenPendingData pendingData = ((HomeScreenActivity)getActivity()).getPendingData();

        productName = (TextView)convertView.findViewById(R.id.textView2);
        productID = (TextView)convertView.findViewById(R.id.textView3);
        productDescription = (TextView)convertView.findViewById(R.id.product_description);
        productPrice = (TextView)convertView.findViewById(R.id.product_price);

        productName.setText(pendingData.getProduct().getName());
        productPrice.setText("Price : $"+pendingData.getProduct().getPrice());
        productPrice.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.black));
        productDescription.setText(pendingData.getProduct().getLongDesc());

        buttonOk = (Button)convertView.findViewById(R.id.button_double_ok);
        buttonSingleOk = (Button)convertView.findViewById(R.id.button_single_ok);
        buttonDown = (Button)convertView.findViewById(R.id.button_down);
        buttonMayBe = (Button)convertView.findViewById(R.id.button_maybe);
        sendOpinion = (Button)convertView.findViewById(R.id.send_Opinion);
        responseText = (EditText)convertView.findViewById(R.id.response_Text);
        sendOpinion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (responseText.getText().toString().length()>0){
                    ConnectionDetector c = new ConnectionDetector(getActivity().getApplicationContext());
                    if(c.isConnectingToInternet()) {
                        postDataToSendOpinionOnProduct();
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(),"No Network",Toast.LENGTH_LONG).show();
                    }
                }else Toast.makeText(getActivity().getApplicationContext(),"Empty Comment",Toast.LENGTH_LONG).show();
            }
        });
        viewSelf = (Button)convertView.findViewById(R.id.button_view_selfie);
    }

    private void postDataToSendOpinionOnProduct() {
        showLoader();
        OpinionService opinionService = new OpinionService();
        OpinionResponseInput opinionResponseInput = new OpinionResponseInput();
        opinionResponseInput.setOpinionReqId(7);
        opinionResponseInput.setResponseCode("W");
        opinionResponseInput.setResponseTxt(responseText.getText().toString());
        opinionResponseInput.setUserId(Integer.valueOf(new AppSharedPreference().getUserID(getActivity().getApplicationContext())));
        String json = toJson(opinionResponseInput);

        Observable<Response<OpinionResponseOutput>> refreshResponseObservable = opinionService.sendOpinionInput(opinionResponseInput);

        refreshResponseObservable.observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).subscribe(new Subscriber<Response<OpinionResponseOutput>>() {
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
            public void onNext(Response<OpinionResponseOutput> requestResponse) {

                if (requestResponse.isSuccessful()) {

                    Toast.makeText(getActivity().getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
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
