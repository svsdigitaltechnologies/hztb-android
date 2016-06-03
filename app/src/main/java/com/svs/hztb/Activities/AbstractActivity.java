package com.svs.hztb.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.svs.hztb.Adapters.SlideMenuAdapter;
import com.svs.hztb.Bean.RegisterResponse;
import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.Interfaces.IDrawerClosed;
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
public abstract class AbstractActivity extends AppCompatActivity{

    protected LoadingBar _loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _loader=new LoadingBar(this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }





    /**
     * Action bar settings are updated
     */
    protected void actionBarSettings(int title) {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_title, null);
        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.action_bar_drawble, null));
        setActionBarTitle(getString(title));
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
        String userId = new AppSharedPreference().getUserID(getApplicationContext());
        if (!userId.isEmpty() || userId.trim().length() > 0 ){
            return true;
        }else return false;
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
