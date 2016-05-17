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

import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.R;

public class RequestOpinionActivity extends AbstractActivity {

    private ListView groupsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_opinion);
        actionBarSettingswithNavigation(R.string.title_activity_request_opinion);
        intalizeDrawer();
        groupsList = getView(R.id.listview_groups);

        initView();
        initialiseHeaderThumb();

        String []items = {"Select From Contacts"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,items);
        groupsList.setAdapter(adapter);
        groupsList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                displayMessage("Working");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void initView() {
        displayMessage("Working");

//        View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.request_opinion_header, null, false);
//        groupsList.addHeaderView(headerView);
    }

}
