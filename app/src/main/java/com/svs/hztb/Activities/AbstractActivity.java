package com.svs.hztb.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.svs.hztb.Bean.RegisterResponse;
import com.svs.hztb.R;
import com.svs.hztb.RestService.ErrorStatus;
import com.svs.hztb.RestService.RegisterService;
import com.svs.hztb.RestService.ServiceGenerator;
import com.svs.hztb.Utils.LoadingBar;

import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Venu Nalla on 22/04/16.
 */
public abstract class AbstractActivity extends AppCompatActivity {

    protected LoadingBar _loader;
    protected String BASE_URL = "http://hztb-dev.us-east-1.elasticbeanstalk.com";
    protected Typeface custom_font;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _loader=new LoadingBar(this);
        custom_font = Typeface.createFromAsset(getAssets(),  "fonts/walkway_ultrabold.ttf");
    }
    /**
     * Action bar settings are updated
     */
    protected void actionBarSettings(int title) {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.action_bar_drawble, null));
        setActionBarTitle(getString(R.string.title_activity_mobile_phone_registration));
    }


    protected <T extends View> T getView(int id) {
        return (T) findViewById(id);
    }


    protected void displayMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void pushActivity(Class className){
        Intent startActivity = new Intent(this,className);
        startActivity(startActivity);
    }

    public void pushActivity(Class className,String number){
        Intent startActivity = new Intent(this,className);
        startActivity.putExtra("NUMBER",number);
        startActivity(startActivity);
    }

    public void showLoader(){
        if(_loader!=null && !_loader.isShowing()){
            _loader.show();
        }
    }

    public void cancelLoader(){
        if(_loader!=null && _loader.isShowing()){
            _loader.cancel();
        }
    }

    protected String getDeviceToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.shared_pref_app), Activity.MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN", "");
        return token;
    }

    protected boolean getLoginState() {
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.shared_pref_app), Activity.MODE_PRIVATE);
        return sharedPreferences.getBoolean(getResources().getString(R.string.login_success), false);
    }

    protected void saveToken(String key,String value){
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.shared_pref_app), Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key,value);
        edit.commit();
    }


    protected void setActionBarTitle(String title){
        View v = getSupportActionBar().getCustomView();
        TextView titleTxtView = (TextView) v.findViewById(R.id.textview_actionbarTitle);
        titleTxtView.setText(title);
    }


    protected boolean isEmailValid(String emailId){
        return Patterns.EMAIL_ADDRESS.matcher(emailId).matches();
    }

    /**
     * Post data for the registration
     */
    protected void postDataForRegistration(final String mobileNumber,final boolean sendAgain) {
        showLoader();

        RegisterService registerService = new RegisterService();
        Observable<Response<RegisterResponse>> registerResponseObservable = registerService.register(mobileNumber);

        registerResponseObservable.observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).subscribe(new Subscriber<Response<RegisterResponse>>() {
            @Override
            public void onCompleted() {
                cancelLoader();
            }

            @Override
            public void onError(Throwable e) {
                cancelLoader();
            }

            @Override
            public void onNext(Response<RegisterResponse> registerResponse) {

                cancelLoader();
                if (registerResponse.isSuccessful()) {
                    if (!sendAgain) {
                        pushActivity(ConfirmRegistration.class, mobileNumber);
                        finish();
                    }else {
                        displayMessage("OTP Successfully Sent Again");
                    }
                }
                else {
                    List<ErrorStatus> listErrorStatus = ServiceGenerator.parseErrorBody(registerResponse);
                    for (ErrorStatus listErrorState : listErrorStatus) {
                        Log.d("Error Message : ", listErrorState.getMessage() + " " + listErrorState.getStatus());
                        displayMessage(listErrorState.getMessage()+"Status :"+listErrorState.getStatus());
                    }

                }
            }


        });
    }

    protected AlertDialog alertDialog()
    {
        AlertDialog alertDialog =new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return alertDialog;

    }
}
