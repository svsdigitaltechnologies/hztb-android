package com.svs.hztb.Activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.svs.hztb.Adapters.RetriveGroupsAdapter;
import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.R;

import java.util.ArrayList;
import java.util.Arrays;

public class NewRequestActivity extends AbstractActivity {

    private ListView groupsList;
    private ArrayList<String> groupsArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_opinion);
        actionBarSettingswithNavigation(R.string.title_activity_request_opinion);
        intalizeDrawer();
        groupsList = getView(R.id.listview_groups);

        initView();

        groupsArrayList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.group_items)));
        groupsArrayList.add("Select From Contacts");

//
//        String []items = {"Select From Contacts"};
        RetriveGroupsAdapter adapter = new RetriveGroupsAdapter(getApplicationContext(),groupsArrayList);
        groupsList.setAdapter(adapter);
        groupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(groupsArrayList.size()-1 == i){
                    pushActivity(ContactsActivity.class);

                }
            }
        });
    }

    private void initView() {
        displayMessage("Working");

//        View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.request_opinion_header, null, false);
//        groupsList.addHeaderView(headerView);
    }

}
