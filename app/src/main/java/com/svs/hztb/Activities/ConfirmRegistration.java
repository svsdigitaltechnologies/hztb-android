package com.svs.hztb.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.svs.hztb.Bean.ValidateOTPResponse;
import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.R;
import com.svs.hztb.RestService.ErrorStatus;
import com.svs.hztb.RestService.RegisterService;
import com.svs.hztb.RestService.ServiceGenerator;
import com.svs.hztb.Utils.ConnectionDetector;

import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ConfirmRegistration extends AbstractActivity {


    private String IMEINumber;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 2001;
    private String mobileNumber;
    private EditText otpText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_registration);
        initViews();
        actionBarSettings(R.string.string_confirmRegistration);

    }

    private void initViews() {
        mobileNumber = getIntent().getStringExtra("NUMBER");
        otpText = getView(R.id.edittext_verification_code);
    }


    /**
     * OnClick Functionality for the Send OTP
     * @param view
     */
    public void onSendOTPAgainButtonClicked(View view){
        ConnectionDetector c = new ConnectionDetector(getApplicationContext());
        if(c.isConnectingToInternet()) {
            postDataForRegistration(mobileNumber,true);
        }else displayMessage("No Network");
    }

    @Override
    public void onBackPressed() {
        android.app.AlertDialog alertDialog = alertDialog();
        alertDialog.show();
    }

    /**
     * On Click button
     * @param view
     */
    public void onVerifyButtonClicked(View view){

        postDataForOTPVerification();
    }

    /**
     * Post data to server
     */
    private void postDataForOTPVerification() {
        showLoader();

        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.d("Id",android_id);

        RegisterService registerService = new RegisterService();
        Observable<Response<ValidateOTPResponse>> validateOTPResponseObservable = registerService.validate(mobileNumber,otpText.getText().toString(),"XXX",android_id,getDeviceToken());

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
                    storeMobileNumberAndUserID(validateOTPResponseResponse.body().getUserId());
                    pushActivity(ProfileActivity.class,mobileNumber);
                    finish();
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

    private void storeMobileNumberAndUserID(String userId) {
        new AppSharedPreference().storeUserID(userId,getApplicationContext());
        new AppSharedPreference().storeMobileNumber(mobileNumber,getApplicationContext());
    }

}


