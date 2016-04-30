package com.svs.hztb.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.svs.hztb.R;
import com.svs.hztb.Utils.LoadingBar;


/**
 * Created by Venu Nalla on 22/04/16.
 */
public abstract class AbstractActivity extends AppCompatActivity {

    protected LoadingBar _loader;
    protected String BASE_URL = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _loader=new LoadingBar(this);
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

    protected void setActionBarTitle(String title){
        View v = getSupportActionBar().getCustomView();
        TextView titleTxtView = (TextView) v.findViewById(R.id.textview_actionbarTitle);
        titleTxtView.setText(title);
    }


    protected boolean isEmailValid(String emailId){
        return Patterns.EMAIL_ADDRESS.matcher(emailId).matches();
    }

}
