package com.svs.hztb.Activities;

import android.content.Context;
import android.provider.Settings;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
        actionBarSettings(R.string.string_confirmRegistration);
        initViews();

    }

    private void initViews() {
        TextView mobileNum = getView(R.id.textView_mobileNumber);
        mobileNumber = getIntent().getStringExtra("NUMBER");
        mobileNum.setText("+" +mobileNumber);
        mobileNum.setTypeface(custom_font);
        otpText = getView(R.id.edittext_verification_code);

        setupCustomFontForTextviews();

    }

    private void setupCustomFontForTextviews() {
        TextView cRegHeader = getView(R.id.textview_confirmRegistrationHeader);
        TextView cRegHeader2 = getView(R.id.textview_enterOTP);
        TextView cRegHeader3 = getView(R.id.textView_timerWarning);
        TextView cRegPhoneText = getView(R.id.registration_phoneNum);
        Button cRegSubmit = getView(R.id.button_verify);
        Button cRegSendOtp = getView(R.id.button_sendOTP);

        otpText.setTypeface(custom_font);
        cRegHeader.setTypeface(custom_font);
        cRegHeader2.setTypeface(custom_font);
        cRegHeader3.setTypeface(custom_font);
        cRegPhoneText.setTypeface(custom_font);
        cRegSendOtp.setTypeface(custom_font);
        cRegSubmit.setTypeface(custom_font);


    }



    /**
     * OnClick Functionality for the Send OTP
     * @param view
     */
    public void onSendOTPAgainButtonClicked(View view){
        postDataForRegistration(mobileNumber,true);
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

}


