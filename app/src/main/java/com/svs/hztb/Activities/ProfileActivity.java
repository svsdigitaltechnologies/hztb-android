package com.svs.hztb.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

public class ProfileActivity extends AbstractActivity {

    private String mobileNumber;
    private EditText emailEditText;
    private EditText nameEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        actionBarSettings(R.string.profile_title);
        initView();
    }

    private void initView() {
        mobileNumber = getIntent().getStringExtra("NUMBER");
        TextView mobileNum = getView(R.id.editText_mobileNumber);
        mobileNum.setText(getResources().getString(R.string.string_plus) + mobileNumber);
        emailEditText = getView(R.id.editText_email);
        nameEditText = getView(R.id.editText_name);
    }

    /**
     * OnClick event for Sign Up
     *
     * @param view
     */
    public void onSignUpDoneButtonClicked(View view) {
        if (isNotEmpty(nameEditText)) {
            if (isEmailValid(emailEditText.getText().toString())) {
                postDataForUpdateUserProfile();
            } else
                displayMessage(getResources().getString(R.string.alert_email_invalid));
        } else
            displayMessage(getResources().getString(R.string.alert_mobilenumber_invalid));
    }

    private void postDataForUpdateUserProfile() {
        showLoader();

        RegisterService registerService = new RegisterService();
        Observable<Response<UserProfileResponse>> userProfileResponseObservable = registerService.updateUserProfile(mobileNumber, nameEditText.getText().toString().trim(), emailEditText.getText().toString().trim());

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
                    displayMessage(getResources().getString(R.string.toast_userprofile_update_success));
                    pushActivity(HomeScreenActivity.class);
                    storeSuccessLoginInSharedPreferences();
                    finish();
                } else {
                    List<ErrorStatus> listErrorStatus = ServiceGenerator.parseErrorBody(userProfileResponseObservable);
                    for (ErrorStatus listErrorState : listErrorStatus) {
                        Log.d("Error Message : ", listErrorState.getMessage() + " " + listErrorState.getStatus());
                        displayMessage(listErrorState.getMessage() + "Status :" + listErrorState.getStatus());
                    }

                }
            }
        });
    }

    private void storeSuccessLoginInSharedPreferences() {
        SharedPreferences sharedpref = getSharedPreferences(getResources().getString(R.string.shared_pref_app), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putBoolean(getResources().getString(R.string.login_success),true);
        editor.commit();
    }

    /**
     * Check if name field is empty or not
     *
     * @param name
     * @return
     */
    private boolean isNotEmpty(EditText name) {

        if (name.getText().toString().trim().equals(""))
            return false;
        else
            return true;
    }

    @Override
    public void onBackPressed() {
        android.app.AlertDialog alertDialog = alertDialog();
        alertDialog.show();
    }

}
