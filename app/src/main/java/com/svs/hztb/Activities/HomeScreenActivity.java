package com.svs.hztb.Activities;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.svs.hztb.R;

public class HomeScreenActivity extends AbstractActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        actionBarSettingswithNavigation(R.string.title_activity_home_screen);
        intalizeDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * OnClick Functionality for the requestOpinion
     * @param view
     */
    public void requestOpinionOnClick(View view){


    }


    /**
     * OnClick Functionality for the respondOpinion
     * @param view
     */
    public void respondOpinionOnClick(View view){

    }

    /**
     * OnClick Functionality for the inStore
     * @param view
     */
    public void inStoreButtonOnClick(View view){

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hztb, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        alertDialog();
    }
}
