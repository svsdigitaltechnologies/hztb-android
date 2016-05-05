package com.svs.hztb.Activities;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.svs.hztb.Bean.RegisterResponse;
import com.svs.hztb.R;
import com.svs.hztb.RestService.ErrorStatus;
import com.svs.hztb.RestService.RegisterService;
import com.svs.hztb.RestService.ServiceGenerator;
import com.svs.hztb.Utils.TextDrawable;

import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class RegistrationActivity extends AbstractActivity {

    private EditText mobileNumber;

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
        mobileNumber = getView(R.id.editText_mobilePhoneNo);
        String code = "+ ";
        mobileNumber.setCompoundDrawablesWithIntrinsicBounds(new TextDrawable(code), null, null, null);
        mobileNumber.setCompoundDrawablePadding(code.length() * 10);
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
