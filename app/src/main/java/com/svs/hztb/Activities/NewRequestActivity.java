package com.svs.hztb.Activities;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class NewRequestActivity extends Fragment {

    private ListView groupsList;
    private ArrayList<String> groupsArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_request_opinion, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        groupsList = (ListView)view.findViewById(R.id.listview_groups);
        initView();
        groupsArrayList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.group_items)));
        groupsArrayList.add("Select From Contacts");

//
//        String []items = {"Select From Contacts"};
        RetriveGroupsAdapter adapter = new RetriveGroupsAdapter(getActivity().getApplicationContext(),groupsArrayList);
        groupsList.setAdapter(adapter);
        groupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(groupsArrayList.size()-1 == i){
//                    pushActivity(ContactsActivity.class);

                }
            }
        });
    }

    private void initView() {
//        displayMessage("Working");

//        View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.request_opinion_header, null, false);
//        groupsList.addHeaderView(headerView);
    }

}
