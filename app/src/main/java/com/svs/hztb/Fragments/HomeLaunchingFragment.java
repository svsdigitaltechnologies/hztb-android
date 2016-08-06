package com.svs.hztb.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.svs.hztb.R;
import com.svs.hztb.Utils.ContactsSync;


public class HomeLaunchingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_launching, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button button = (Button)view.findViewById(R.id.button_getOpinons);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetOpinionsFragment fragment = new GetOpinionsFragment();
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
        });

        Button button1 = (Button)view.findViewById(R.id.button5);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactsSync syncCont = new ContactsSync(getActivity());
                syncCont.syncContactsToServer();
            }
        });

        Button opinionGiven = (Button)view.findViewById(R.id.button4);
        opinionGiven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpinionGivenFragment fragment = new OpinionGivenFragment();
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
        });
    }
}
