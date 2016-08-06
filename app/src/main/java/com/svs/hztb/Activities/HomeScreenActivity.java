package com.svs.hztb.Activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.svs.hztb.Adapters.SlideMenuAdapter;
import com.svs.hztb.Bean.GivenPendingData;
import com.svs.hztb.Bean.OpinionCountData;
import com.svs.hztb.Bean.OpinionData;
import com.svs.hztb.Bean.Product;
import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.Fragments.GetOpinionsFragment;
import com.svs.hztb.Fragments.GroupsFragment;
import com.svs.hztb.Fragments.HomeLaunchingFragment;
import com.svs.hztb.Fragments.NewRequestFragment;
import com.svs.hztb.Fragments.NotificationFragment;
import com.svs.hztb.Fragments.OpinionGivenFragment;
import com.svs.hztb.Fragments.ProfileFragment;
import com.svs.hztb.Interfaces.IDrawerClosed;
import com.svs.hztb.Permissions.MarshMallowPermission;
import com.svs.hztb.R;

public class HomeScreenActivity extends AbstractActivity implements IDrawerClosed {
    private final int SLIDE_TIMEOUT = 200;

    public SlideMenuAdapter slideMenuAdapter;
    protected DrawerLayout mDrawerLayout;
    protected String[] menuItems;
    protected ListView mDrawerList;
    protected OpinionData mProduct;
    protected OpinionCountData opinionCountData;
    protected GivenPendingData givenPendingData;
    private Uri capturedImageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        actionBarSettingswithNavigation(R.string.title_activity_home_screen);
        intalizeDrawer();
    }


    protected void intalizeDrawer() {
        moveDrawerToTop();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menuItems = getResources().getStringArray(R.array.side_menu_items);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        slideMenuAdapter = new SlideMenuAdapter(getApplicationContext(), menuItems, this);
        mDrawerList.setAdapter(slideMenuAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               selectItem(i);
            }
        });



        Intent intent =getIntent();
        boolean isOpinionRequest = getIntent().getBooleanExtra("request",false);
        boolean isOpinionResponse =getIntent().getBooleanExtra("response",false);
        if (isOpinionRequest){
            Fragment fragment = new OpinionGivenFragment();
            pushFragment(fragment);
        }else if (isOpinionResponse){
            Fragment fragment = new GetOpinionsFragment();
            pushFragment(fragment);
        }
        String is = getIntent().getStringExtra("true");
    }



    public Uri getCapturedImageURI() {
        return capturedImageURI;
    }

    public void setCapturedImageURI(Uri capturedImageURI) {
        this.capturedImageURI = capturedImageURI;
    }




    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(final int position) {
        // Create a new fragment and specify the planet to show based on position
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
        Fragment fragment = null;

        switch (position){
            case 1:{

                int count = getFragmentManager().getBackStackEntryCount();
                for(int i = 0; i < count; ++i) {
                    getFragmentManager().popBackStack();
                }
                break;
            }
            case 2:{
                fragment = new ProfileFragment();
                pushFragment(fragment);
                break;
            }
            case 3:{
                fragment = new NotificationFragment();
                pushFragment(fragment);
                break;
            }
            case 4:{
                fragment = new GroupsFragment();
                pushFragment(fragment);
                break;
            }

            case 5 :{
                fragment = new NewRequestFragment();
                pushFragment(fragment);
                break;
            }
            case 6:{
                logOut();
                break;
            }
        }
        mDrawerList.setItemChecked(position, true);

    }

    private void logOut() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to Logout")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        new AppSharedPreference().storeUserID("",getApplicationContext());
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode== MarshMallowPermission.CAMERA_PERMISSION_REQUEST_CODE)
        {
            displayMessage("Camera");
        }
    }


    private void pushFragment(final Fragment fragment) {

        final Fragment currentFragment = getFragmentManager().findFragmentById(R.id.fragment);
        if (!currentFragment.getClass().toString().equals(fragment.getClass().toString())) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String backStateName = fragment.getClass().getName();
                    FragmentManager fragmentManager = getFragmentManager();
                    boolean fragmentPopped = fragmentManager
                            .popBackStackImmediate(backStateName, 0);
                    if (!fragmentPopped) {
                        FragmentTransaction ftx = fragmentManager.beginTransaction();
                        ftx.replace(R.id.fragment, fragment);
                        ftx.addToBackStack(backStateName);
                        ftx.commit();
                    }
                    // Highlight the selected item, update the title, and close the drawer

                }
            }, SLIDE_TIMEOUT);
        }
    }




    public void onDrawerClosedClicked() {
        mDrawerLayout.closeDrawers();
    }

    private void moveDrawerToTop() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DrawerLayout drawer = (DrawerLayout) inflater.inflate(R.layout.decor, null); // "null" is important.

        // HACK: "steal" the first child of decor view
        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        View child = decor.getChildAt(0);
        decor.removeView(child);
        RelativeLayout container = (RelativeLayout) drawer.findViewById(R.id.content_frame); // This is the container we defined just now.
        container.addView(child, 0);
        drawer.findViewById(R.id.left_drawer).setPadding(0, getStatusBarHeight(), 0, 0);
        // Make the drawer replace the first child
        decor.addView(drawer);
    }

    /**
     * Action bar settings are updated
     */
    protected void actionBarSettingswithNavigation(int title) {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_title, null);
        getSupportActionBar().setCustomView(mCustomView);

        TextView titleView = (TextView) mCustomView.findViewById(R.id.textview_actionbarTitle);
        titleView.setVisibility(View.INVISIBLE);

        RelativeLayout actionBarWithoutNavigation = (RelativeLayout) mCustomView.findViewById(R.id.layout_back_actionbar);
        actionBarWithoutNavigation.setVisibility(View.VISIBLE);

        final ImageView navDrawer = (ImageView) mCustomView.findViewById(R.id.button_nav);
        navDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        TextView customTitle = (TextView) mCustomView.findViewById(R.id.titleView);
        customTitle.setText(getResources().getString(title));

        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.action_bar_drawble, null));
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mDrawerList)){
            mDrawerLayout.closeDrawer(mDrawerList);
        }
        else if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * OnClick Functionality for the requestOpinion
     *
     * @param view
     */
    public void requestOpinionOnClick(View view) {


    }


    /**
     * OnClick Functionality for the respondOpinion
     *
     * @param view
     */
    public void respondOpinionOnClick(View view) {

    }

    /**
     * OnClick Functionality for the inStore
     *
     * @param view
     */
    public void inStoreButtonOnClick(View view) {

    }

    public void setOpinionData(OpinionCountData opinionCountDataa ){
        opinionCountData = opinionCountDataa;
    }
    public OpinionCountData getOpinionCountData(){
        return opinionCountData;
    }


    public void setGivenPendingData(GivenPendingData pendingData){
        givenPendingData = pendingData;
    }
    public GivenPendingData getPendingData(){
        return givenPendingData;
    }

    public void setProduct(OpinionData product){
        this.mProduct = product;
    }
    public OpinionData getProduct(){
        return mProduct;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hztb, menu);
        return true;
    }

}
