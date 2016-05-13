package com.svs.hztb.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.svs.hztb.Adapters.SpinnerAdapter;
import com.svs.hztb.CustomViews.WalkWayButton;
import com.svs.hztb.R;


public class RegistrationActivity extends AbstractActivity {

    private EditText mobileNumber;
    private Spinner countrySpinner;
    private int selectedIndex;

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
        final TextView countryCode = getView(R.id.textview_registration_countryCode);
        countrySpinner = getView(R.id.spinner);
        countrySpinner.setAdapter(new SpinnerAdapter(RegistrationActivity.this, R.layout.custom_spinner_item,
                getResources().getStringArray(R.array.countries)));
        countrySpinner.setBackgroundResource(android.R.color.transparent);
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String plus = getResources().getString(R.string.string_plus);
                countryCode.setText(plus + String.valueOf(getResources().getIntArray(R.array.countryCodeArray)[i]));
                selectedIndex = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void showAlertDialog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        View dialog = inflater.inflate(R.layout.custom_alert_verify, null);
        dialogBuilder.setView(dialog);

        final AlertDialog alertDialog = dialogBuilder.create();

        TextView mobileNum = (TextView) dialog.findViewById(R.id.textview_mobile_number);

        String plus = getResources().getString(R.string.string_plus);
        mobileNum.setText(plus + String.valueOf(getResources().getIntArray(R.array.countryCodeArray)[selectedIndex] +" "+ mobileNumber.getText().toString()));

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
                String phoneNum = String.valueOf(getResources().getIntArray(R.array.countryCodeArray)[selectedIndex]) + mobileNumber.getText().toString().trim();
                postDataForRegistration(phoneNum, false);
            }
        });

        alertDialog.show();

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
