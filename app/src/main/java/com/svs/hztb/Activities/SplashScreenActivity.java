package com.svs.hztb.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.svs.hztb.PushNotifications.RegistrationIntentService;
import com.svs.hztb.R;



public class SplashScreenActivity extends AbstractActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        checkForDeviceRegistrationToken();




         /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                if (getLoginState()){
                     pushActivity(HomeScreenActivity.class);
                }else {
                      pushActivity(RegistrationActivity.class);
                }
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    /**
     * Method used to get the Device Registration Id .
     */
    private void checkForDeviceRegistrationToken() {
        if (getDeviceToken().equalsIgnoreCase("")) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }
}
