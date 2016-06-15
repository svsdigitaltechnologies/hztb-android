package com.svs.hztb.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.svs.hztb.Adapters.RetriveGroupsAdapter;
import com.svs.hztb.Bean.Contact;
import com.svs.hztb.Bean.ContactGroup;
import com.svs.hztb.Database.DatabaseHandler;
import com.svs.hztb.R;

import java.util.ArrayList;
import java.util.Arrays;

public class NewRequestFragment extends Fragment {

    private ListView groupsList;
    private ArrayList<ContactGroup> groupsArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_request, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupsList = (ListView) view.findViewById(R.id.listview_groups);

        DatabaseHandler db  = new DatabaseHandler(getActivity().getApplicationContext());

        groupsArrayList = db.getGroupNames();
        ContactGroup group = new ContactGroup();
        group.setGroupName("Select From Contacts");
        groupsArrayList.add(group);

        final RetriveGroupsAdapter adapter = new RetriveGroupsAdapter(getActivity().getApplicationContext(), groupsArrayList);
        groupsList.setAdapter(adapter);
        groupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (groupsArrayList.size() - 1 == i) {
                    ContactsFragment fragment = new ContactsFragment();
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
                }
                else {
//                    DatabaseHandler db  = new DatabaseHandler(getActivity().getApplicationContext());
//                    ContactGroup group = db.getGroupInfo(groupsArrayList.get(i).getGroupName());
                    if (!groupsArrayList.get(i).isSelect()) {
                        groupsArrayList.get(i).setSelect(true);
                    }else groupsArrayList.get(i).setSelect(false);

                    adapter.notifyDataSetChanged();
//                    Toast.makeText(getActivity().getApplicationContext(),group.getGroupName(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}
