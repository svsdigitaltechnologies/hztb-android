package com.svs.hztb.Activities;

import android.content.Context;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.svs.hztb.Bean.RegisterResponse;
import com.svs.hztb.Bean.ValidateOTPResponse;
import com.svs.hztb.R;
import com.svs.hztb.RestService.ErrorStatus;
import com.svs.hztb.RestService.RegisterService;
import com.svs.hztb.RestService.ServiceGenerator;

import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ConfirmRegistration extends AbstractActivity {

    private String mobileNumber;
    private EditText otpText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_registration);
        actionBarSettings();
        initViews();

    }

    private void initViews() {
        TextView mobileNum = getView(R.id.textView_mobileNumber);
        mobileNumber = getIntent().getStringExtra("NUMBER");
        mobileNum.setText("+" +mobileNumber);
        otpText = getView(R.id.edittext_verification_code);
    }

    /**
     Action bar settings are updated
     */
    private void actionBarSettings() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        setActionBarTitle(getString(R.string.string_confirmRegistration));
    }

    public void onVerifyButtonClicked(View view){
        postDataForOTPVerification();
    }

    private void postDataForOTPVerification() {
        showLoader();

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        Log.d("IMEI",telephonyManager.getDeviceId());
        Log.d("TOKEN",getDeviceToken());


        RegisterService registerService = new RegisterService();
        Observable<Response<ValidateOTPResponse>> validateOTPResponseObservable = registerService.validate(mobileNumber,otpText.getText().toString(),telephonyManager.getDeviceId(),getDeviceToken());

        validateOTPResponseObservable.observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).subscribe(new Subscriber<Response<ValidateOTPResponse>>() {
            @Override
            public void onCompleted() {
                cancelLoader();
            }

            @Override
            public void onError(Throwable e) {
                cancelLoader();
            }

            @Override
            public void onNext(Response<ValidateOTPResponse> validateOTPResponseResponse) {

                cancelLoader();
                if (validateOTPResponseResponse.isSuccessful()) {
                    pushActivity(SignUpActivity.class,mobileNumber);
                }
                else {
                    List<ErrorStatus> listErrorStatus = ServiceGenerator.parseErrorBody(validateOTPResponseResponse);
                    for (ErrorStatus listErrorState : listErrorStatus) {
                        Log.d("Error Message : ", listErrorState.getMessage() + " " + listErrorState.getStatus());
                        displayMessage(listErrorState.getMessage()+"Status :"+listErrorState.getStatus());
                    }

                }
            }


        });
    }

}


