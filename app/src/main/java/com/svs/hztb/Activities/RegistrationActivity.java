package com.svs.hztb.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.svs.hztb.Bean.UserProfileRequest;
import com.svs.hztb.Bean.UserProfileRequests;
import com.svs.hztb.Bean.UserProfileResponse;
import com.svs.hztb.Bean.UserProfileResponses;
import com.svs.hztb.CustomViews.WalkWayButton;
import com.svs.hztb.R;
import com.svs.hztb.RestService.RegisterService;
import com.svs.hztb.Utils.ConnectionDetector;
import com.svs.hztb.Utils.Constants;
import com.svs.hztb.Utils.ContactsSync;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class RegistrationActivity extends AbstractActivity {

    private EditText mobileNumber;
    private Button countryCodeButton;
    private TextView countryCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        actionBarSettings(R.string.title_activity_mobile_phone_registration);
        initViews();

    }

    /**
     * Initialize the views
     */
    private void initViews() {
        mobileNumber = getView(R.id.editText_mobilePhoneNo);
        countryCodeButton = getView(R.id.country_code_button);
        countryCode = getView(R.id.textview_registration_countryCode);
        countryCode.setText(getResources().getString(R.string.string_plus)+String.valueOf(getResources().getIntArray(R.array.countryCodeArray)[7]));
        countryCodeButton.setText(getResources().getStringArray(R.array.countries)[7]);

        ContactsSync syncCont = new ContactsSync(RegistrationActivity.this);
        syncCont.syncContactsToServer();
    }




    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == Constants.MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
            // Received permission result for READ_PHONE_STATE permission.est.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new ContactsSync(RegistrationActivity.this).doPermissionGrantedStuffs();
            } else {
                alertAlert(getString(R.string.permissions_not_granted_read_phone_state));

            }
        }
    }

    private void alertAlert(String msg) {
        new AlertDialog.Builder(RegistrationActivity.this)
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                       activity.finish();
                    }
                })
                .show();
    }



    private void showAlertDialog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        View dialog = inflater.inflate(R.layout.custom_alert_verify, null);
        dialogBuilder.setView(dialog);

        final AlertDialog alertDialog = dialogBuilder.create();

        TextView mobileNum = (TextView) dialog.findViewById(R.id.textview_mobile_number);

        mobileNum.setText(String.valueOf(countryCode.getText().toString()+" "+ mobileNumber.getText().toString()));

        WalkWayButton editButton = (WalkWayButton)dialog.findViewById(R.id.button_edit);
        WalkWayButton okButton = (WalkWayButton)dialog.findViewById(R.id.button_ok);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                String phoneNum = countryCode.getText().toString().replace("+","").trim() + mobileNumber.getText().toString().trim();
                ConnectionDetector c = new ConnectionDetector(getApplicationContext());
                if(c.isConnectingToInternet()) {
                    postDataForRegistration(phoneNum, false);
                }else displayMessage("No Network");
            }
        });

        alertDialog.show();

    }

    /**
     * On click action performed
     */
    public void onCountryCodeClick(View view) {
        Intent intent = new Intent(RegistrationActivity.this,CountryCodeActivity.class);
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==100)
        {
            String countryName=data.getStringExtra("COUNTRYNAME");
            int countryCodes = data.getIntExtra("CODE",0);
            countryCodeButton.setText(countryName);
            countryCode.setText(getResources().getString(R.string.string_plus)+countryCodes);
        }
    }


    /**
     * On click action performed
     */
    public void onSubmitClicked(View view) {
        if (isValidMobile()) {
            showAlertDialog();
        } else {
            displayMessage(getResources().getString(R.string.alert_mobilenumber_invalid));
        }
    }

    @Override
    public void onBackPressed() {
        android.app.AlertDialog alertDialog = alertDialog();
        alertDialog.show();
    }

    /**
     * Check if the mobile number entered is valid or not.
     *
     * @return
     */
    private boolean isValidMobile() {
        if (mobileNumber.getText().toString().length() == 10) {
            return true;
        } else return false;
    }
}
