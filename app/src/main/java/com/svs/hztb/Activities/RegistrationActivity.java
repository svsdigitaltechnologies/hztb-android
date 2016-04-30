package com.svs.hztb.Activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.svs.hztb.R;

public class RegistrationActivity extends AbstractActivity {

    private EditText mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        setActionBarTitle(getString(R.string.title_activity_mobile_phone_registration));
        mobileNumber = getView(R.id.editText_mobilePhoneNo);
    }

    /*
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyaction = event.getAction();

        if(keyaction == KeyEvent.ACTION_DOWN)
        {
            int keycode = event.getKeyCode();
            int keyunicode = event.getUnicodeChar(event.getMetaState() );
            char character = (char) keyunicode;
            if (mobileNumber.getText().toString().length() == 0){
                String number ="+"+mobileNumber.getText().toString();
                Log.d(getPackageName().toString(),number);
                mobileNumber.setText(number);
            }

        }

        return super.dispatchKeyEvent(event);
    }
    */ 
}
