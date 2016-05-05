package com.svs.hztb.Activities;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.svs.hztb.Adapters.SpinnerAdapter;
import com.svs.hztb.R;


public class RegistrationActivity extends AbstractActivity {

    private EditText mobileNumber;
    private Spinner countrySpinner;
    // Declaring the String Array with the Text Data for the Spinners
    String[] Countries = { "United States", "India" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        actionBarSettings();
        initViews();
    }

    /**
     * Initialize the views
     */
    private void initViews() {
//        mobileNumber = getView(R.id.editText_mobilePhoneNo);
        countrySpinner = getView(R.id.spinner);
        countrySpinner.setAdapter(new SpinnerAdapter(RegistrationActivity.this,R.layout.custom_spinner_item,
                Countries));
    }
   /**
      Action bar settings are updated
    */
    private void actionBarSettings() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        setActionBarTitle(getString(R.string.title_activity_mobile_phone_registration));
    }

    /**
    On click action performed
     */
   public void onSubmitClicked(View view){
      if (isValidMobile()){
            postDataForRegistration(mobileNumber.getText().toString(),false);
      }else {
          displayMessage("Mobile Number Invalid");
      }
   }

    @Override
    public void onBackPressed() {
        android.app.AlertDialog alertDialog = alertDialog();
        alertDialog.show();
    }

    /**
     * Check if the mobile number entered is valid or not.
     * @return
     */
    private boolean isValidMobile()
    {
        if (mobileNumber.getText().toString().length() > 9 && mobileNumber.getText().toString().length() <=13){
            return true;
        }else return false;
    }
}
