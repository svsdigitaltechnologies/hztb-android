package com.svs.hztb.Activities;

import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.svs.hztb.Adapters.SpinnerAdapter;
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
                countryCode.setTypeface(custom_font);
                countryCode.setText(plus + String.valueOf(getResources().getIntArray(R.array.countryCodeArray)[i]));
                selectedIndex = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        setupCustomFontForTextviews();

    }

    private void setupCustomFontForTextviews() {
        mobileNumber.setTypeface(custom_font);
        TextView regHeader = getView(R.id.registration_header1);
        TextView regHeader2 = getView(R.id.registration_header2);
        TextView regHeader3 = getView(R.id.registration_header3);
        TextView regPhoneText = getView(R.id.registration_phoneNum);
        Button regSubmit = getView(R.id.button_submit);

        regSubmit.setTypeface(custom_font);
        regHeader.setTypeface(custom_font);
        regHeader2.setTypeface(custom_font);
        regHeader3.setTypeface(custom_font);
        regPhoneText.setTypeface(custom_font);

     }

    /**
     * On click action performed
     */
    public void onSubmitClicked(View view) {
        if (isValidMobile()) {
            String phoneNum = String.valueOf(getResources().getIntArray(R.array.countryCodeArray)[selectedIndex]) + mobileNumber.getText().toString().trim();
            postDataForRegistration(phoneNum, false);
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
