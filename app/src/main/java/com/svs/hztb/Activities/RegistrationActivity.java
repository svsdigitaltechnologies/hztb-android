package com.svs.hztb.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.svs.hztb.CustomViews.WalkWayButton;
import com.svs.hztb.R;


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
                postDataForRegistration(phoneNum, false);
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
