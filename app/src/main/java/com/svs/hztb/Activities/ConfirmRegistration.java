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
        actionBarSettings(R.string.string_confirmRegistration);
        initViews();
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
     * Called when the 'loadIMEI' function is triggered.
     */
    public void loadIMEI() {
        // Check if the READ_PHONE_STATE permission is already available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // READ_PHONE_STATE permission has not been granted.
            requestReadPhoneStatePermission();
        } else {
            // READ_PHONE_STATE permission is already been granted.
            doPermissionGrantedStuffs();
        }
    }



    /**
     * Requests the READ_PHONE_STATE permission.
     * If the permission has been denied previously, a dialog will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            new AlertDialog.Builder(ConfirmRegistration.this)
                    .setTitle("Permission Request")
                    .setMessage(getString(R.string.permission_read_phone_state_rationale))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //re-request
                            ActivityCompat.requestPermissions(ConfirmRegistration.this,
                                    new String[]{Manifest.permission.READ_PHONE_STATE},
                                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                        }
                    })
                    .show();
        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            // Received permission result for READ_PHONE_STATE permission.est.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has been granted, proceed with displaying IMEI Number
                //alertAlert(getString(R.string.permision_available_read_phone_state));
                doPermissionGrantedStuffs();
            } else {
                alertAlert(getString(R.string.permissions_not_granted_read_phone_state));
            }
        }
    }

    private void alertAlert(String msg) {
        new AlertDialog.Builder(ConfirmRegistration.this)
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }


    public void doPermissionGrantedStuffs() {
        //Have an  object of TelephonyManager
        TelephonyManager tm =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        //Get IMEI Number of Phone  //////////////// for this example i only need the IMEI
        IMEINumber=tm.getDeviceId();

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
                    new AppSharedPreference().storeUserID(validateOTPResponseResponse.body().getUserId(),getApplicationContext());
                    Log.d("UserID",validateOTPResponseResponse.body().getUserId());
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

}


