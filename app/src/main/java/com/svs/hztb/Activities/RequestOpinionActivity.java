package com.svs.hztb.Activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
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

        String []items = {"Family","Friends"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,items);

        // Assign adapter to ListView
        groupsList.setAdapter(adapter);

    }

    private void initView() {


        View footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.request_opinion_select_contact_footer, null, false);
        Button selectContactsButton = (Button) footerView.findViewById(R.id.button_select_contacts);
        selectContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayMessage("Working");
            }
        });
        groupsList.addFooterView(footerView);

//        View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.request_opinion_header, null, false);
//        groupsList.addHeaderView(headerView);
    }

}
