package com.svs.hztb.Activities;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.svs.hztb.Bean.RegisterResponse;
import com.svs.hztb.Bean.UserProfileResponse;
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

public class SignUpActivity extends AbstractActivity {

    private String mobileNumber;
    private EditText emailEditText;
    private EditText nameEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        actionBarSettings();

        initView();


    }

    private void initView() {
        mobileNumber = getIntent().getStringExtra("NUMBER");
        EditText mobileNum = getView(R.id.editText_mobileNumber);
        mobileNum.setText("+"+mobileNumber);
        emailEditText = getView(R.id.editText_email);
        nameEditText = getView(R.id.editText_name);
    }

    /**
     * Action bar settings are updated
     */
    private void actionBarSettings() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        setActionBarTitle(getString(R.string.signUp_title));
    }

    /**
     * OnClick event for Facebook Login
     * @param view
     */
    public void onFacebookButtonClicked(View view){

    }
    /**
     * OnClick event for Google+ Login
     * @param view
     */
    public void onGooglePlusButtonClicked(View view){

    }

    /**
     * OnClick event for Twitter Login
     * @param view
     */
    public void onTwitterButtonClicked(View view){

    }

    /**
     * OnClick event for Sign Up
     * @param view
     */
    public void onSignUpDoneButtonClicked(View view){
        if (isNotEmpty(nameEditText)){
            if(isEmailValid(emailEditText.getText().toString())){
                postDataForUpdateUserProfile();
            }else
                displayMessage("Please Enter Valid Email ID ");
        }else
            displayMessage("Please Enter Valid Name");
        }

    private void postDataForUpdateUserProfile() {
        showLoader();

        RegisterService registerService = new RegisterService();
        Observable<Response<UserProfileResponse>> userProfileResponseObservable = registerService.updateUserProfile(mobileNumber,nameEditText.getText().toString(),emailEditText.getText().toString());

        userProfileResponseObservable.observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).subscribe(new Subscriber<Response<UserProfileResponse>>() {
            @Override
            public void onCompleted() {
                cancelLoader();
            }

            @Override
            public void onError(Throwable e) {
                cancelLoader();
            }

            @Override
            public void onNext(Response<UserProfileResponse> userProfileResponseObservable) {

                cancelLoader();
                if (userProfileResponseObservable.isSuccessful()) {
                        displayMessage("User Profile Updated Successfully");
                  }
                else {
                    List<ErrorStatus> listErrorStatus = ServiceGenerator.parseErrorBody(userProfileResponseObservable);
                    for (ErrorStatus listErrorState : listErrorStatus) {
                        Log.d("Error Message : ", listErrorState.getMessage() + " " + listErrorState.getStatus());
                        displayMessage(listErrorState.getMessage()+"Status :"+listErrorState.getStatus());
                    }

                }
            }


        });
    }

    /**
     *  Check if name field is empty or not
     * @param name
     * @return
     */
    private boolean isNotEmpty(EditText name){

       if (name.getText().toString().trim().equals(""))
            return false;
        else
            return true;
    }

}
