package com.svs.hztb.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.svs.hztb.Adapters.NotificationAdapter;
import com.svs.hztb.Adapters.NotificationRingtoneAdapter;
import com.svs.hztb.Bean.NotificationModel;
import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.R;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;


public class NotificationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int NOTIFICATION_TITLE = 0;
    public static final int NOTIFICATION_URI = 1;
    public static final int NOTIFICATION_ID = 2;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Ringtone ringtone;
    private ListView notificationListView;
    private ArrayList<NotificationModel> notificationModelArrayList;
    private String[] selectedRingtone;
    private NotificationAdapter notificationAdapter;
    private int ringtonePosition;
    private boolean ringtoneSelected;
    private CheckBox conversationCheckBox;
    AppSharedPreference sharedPreference ;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HBNotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_hbnotification, container, false);
        sharedPreference = new AppSharedPreference();

        initUI(rootView);

        LayoutInflater headerInflater = getActivity().getLayoutInflater();
        ViewGroup header = (ViewGroup) headerInflater.inflate(R.layout.layout_notification_item_header, notificationListView, false);
        TextView notificationHeader = (TextView) header.findViewById(R.id.notification_header);
        Typeface walkwayFont = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/walkway_ultrabold.ttf");

        notificationHeader.setTypeface(walkwayFont);
        notificationListView.addHeaderView(header, null, false);
        notificationAdapter = new NotificationAdapter(getActivity().getApplicationContext(), notificationModelArrayList);
        notificationListView.setAdapter(notificationAdapter);
        notificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                showOptionsForSelections(view, position);
            }
        });


        return rootView;
    }

    private void showOptionsForSelections(View view, int position) {
        if (position == 1) {
            ArrayList<String[]> getListOfRingTones = getAllNotificationsRingtonesList();
            showDialogForListOfNotificationRingTones(getListOfRingTones);
        } else if (position == 2) {
            String[] vibrateOptions = getResources().getStringArray(R.array.vibrate_items);
            showDialogwithRadioButtons(R.layout.layout_notification_alertdialog_radiobuttons, vibrateOptions ,true);
        } else {
            String[] popUpNotificationOptions = getResources().getStringArray(R.array.popup_items);
            showDialogwithRadioButtons(R.layout.layout_notification_alertdialog_radiobuttons, popUpNotificationOptions ,false);
        }

    }

    private void showDialogwithRadioButtons(int layoutID, final String[] options, final boolean isVibrateAlertView) {
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layoutID);

        TextView dialogTitle = (TextView)dialog.findViewById(R.id.radioButton_title);
        dialogTitle.setText(options[0]);

        // set the custom dialog components - text, image and button
        RadioGroup radioGroup = (RadioGroup)dialog.findViewById(R.id.radio_group);
        final RadioButton radioButton1 = (RadioButton)dialog.findViewById(R.id.radioButton_one);
        radioButton1.setText(options[1]);
        final RadioButton radioButton2 = (RadioButton)dialog.findViewById(R.id.radioButton_two);
        radioButton2.setText(options[2]);
        final RadioButton radioButton3= (RadioButton)dialog.findViewById(R.id.radioButton_three);
        radioButton3.setText(options[3]);
        final RadioButton radioButton4 = (RadioButton)dialog.findViewById(R.id.radioButton_four);
        radioButton4.setText(options[4]);


        int positionChecked;
        if (isVibrateAlertView) {
            positionChecked = sharedPreference.getStoreNotificationSoundposition(getActivity().getApplicationContext());
        } else {
            positionChecked = sharedPreference.getStoreNotificationPopUpposition(getActivity().getApplicationContext());
        }
        if (positionChecked == 0) {
            radioButton1.setChecked(true);
        } else if (positionChecked == 1) {
            radioButton2.setChecked(true);
        } else if (positionChecked == 2) {
            radioButton3.setChecked(true);
        } else {
            radioButton4.setChecked(true);
        }


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.radioButton_one) {
                    saveToSharedPreferenceAndUpdateListView(radioButton1.getText().toString(), isVibrateAlertView, 0);
                } else if (checkedId == R.id.radioButton_two) {
                    saveToSharedPreferenceAndUpdateListView(radioButton2.getText().toString(), isVibrateAlertView, 1);
                } else if (checkedId == R.id.radioButton_three) {
                    saveToSharedPreferenceAndUpdateListView(radioButton3.getText().toString(), isVibrateAlertView, 2);
                } else {
                    saveToSharedPreferenceAndUpdateListView(radioButton4.getText().toString(), isVibrateAlertView, 3);
                }
                dialog.dismiss();

            }

        });



        Button cancelButton = (Button) dialog.findViewById(R.id.radioButton_canncel);
        // if button is clicked, close the custom dialog
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //Grab the window of the dialog, and change the width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.show();
    }

    private void saveToSharedPreferenceAndUpdateListView(String message, boolean isVibrateAlertView, int position) {
        if (isVibrateAlertView) {
            notificationModelArrayList.get(1).setNotificationSubItemLabel(message);
            sharedPreference.storeNotificationSoundposition(getActivity().getApplicationContext(), position);
            sharedPreference.storeNotificationSound(getActivity().getApplicationContext(), message);
        } else {
            notificationModelArrayList.get(2).setNotificationSubItemLabel(message);
            sharedPreference.storeNotificationPopUpposition(getActivity().getApplicationContext(), position);
            sharedPreference.storeNotificationPopUp(getActivity().getApplicationContext(), message);
        }
        notificationAdapter = new NotificationAdapter(getActivity().getApplicationContext(), notificationModelArrayList);
        notificationListView.setAdapter(notificationAdapter);
        notificationAdapter.notifyDataSetChanged();
    }

    private void showDialogForListOfNotificationRingTones(final ArrayList<String[]> getListOfRingTones) {

        ArrayList<String> notificationNamesList = new ArrayList<>();
        Iterator<String[]> tonesIterator = getListOfRingTones.iterator();
        while (tonesIterator.hasNext()) {
            String[] notificationItem = tonesIterator.next();
            notificationNamesList.add(notificationItem[0]);
        }
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_notification_ringtone_view);
        // set the custom dialog components - text, image and button
        selectedRingtone = null;
        ListView tonesListView = (ListView) dialog.findViewById(R.id.listview_notification_tones_list);
        final NotificationRingtoneAdapter adapter = new NotificationRingtoneAdapter(getActivity().getApplicationContext(), notificationNamesList);
        tonesListView.setAdapter(adapter);

        Button okButton = (Button) dialog.findViewById(R.id.notification_tones_ok_button);
        // if button is clicked, close the custom dialog
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ringtone != null) {
                    if (ringtone.isPlaying()) {
                        ringtone.stop();
                    }
                }
                storeTheRingtoneInSharedPreferenceAndUpdateListView();
                dialog.dismiss();
            }
        });

        Button cancelButton = (Button) dialog.findViewById(R.id.notification_tones_cancel_button);
        // if button is clicked, close the custom dialog
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ringtone != null) {
                    if (ringtone.isPlaying()) {
                        ringtone.stop();
                    }
                }
                dialog.dismiss();
            }
        });

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (ringtone != null) {
                        if (ringtone.isPlaying()) {
                            ringtone.stop();
                        }
                    }
                    dialog.dismiss();
                }
                return true;
            }
        });


        tonesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    ringtonePosition = position;
                    ringtoneSelected = true;
                    adapter.setSelectedIndex(position);
                    String[] notificationRingtone = getListOfRingTones.get(position);
                    Uri notificationRingtoneURI = Uri.parse(notificationRingtone[NOTIFICATION_URI] + "/" + notificationRingtone[NOTIFICATION_ID]);
                    if (ringtone != null) {
                        if (ringtone.isPlaying()) {
                            ringtone.stop();
                        }
                    }
                    ringtone = RingtoneManager.
                            getRingtone(getActivity().getApplicationContext(), notificationRingtoneURI);
                    ringtone.play();
                    selectedRingtone = notificationRingtone;
                    sharedPreference.storeNotificationRingtoneURI(getActivity().getApplicationContext(), notificationRingtone[NOTIFICATION_URI] + "/" + notificationRingtone[NOTIFICATION_ID]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ringtoneSelected = false;
        String[] notificationRingtone = getListOfRingTones.get(0);
        selectedRingtone = notificationRingtone;
        int selectedRingtonePosition = sharedPreference.getStoreNotificationRingToneposition(getActivity().getApplicationContext());
        adapter.setSelectedIndex(selectedRingtonePosition);
        dialog.show();
    }

    private void storeTheRingtoneInSharedPreferenceAndUpdateListView() {
        if (ringtoneSelected) {
            sharedPreference.storeNotificationRingToneposition(getActivity().getApplicationContext(), ringtonePosition);
            sharedPreference.storeNotificationRingTone(getActivity().getApplicationContext(), "Default ringtone " + "(" + selectedRingtone[NOTIFICATION_TITLE] + ")");
        }
        String itemName = sharedPreference.getStoreNotificationRingTone(getActivity().getApplicationContext());
        notificationModelArrayList.get(0).setNotificationSubItemLabel(sharedPreference.getStoreNotificationRingTone(getActivity().getApplicationContext()));
        notificationAdapter = new NotificationAdapter(getActivity().getApplicationContext(), notificationModelArrayList);
        notificationListView.setAdapter(notificationAdapter);
        notificationAdapter.notifyDataSetChanged();
    }


    public ArrayList<String[]> getAllNotificationsRingtonesList() {
        RingtoneManager manager = new RingtoneManager(getActivity().getApplicationContext());
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = manager.getCursor();
        boolean isFirst = true;
        ArrayList<String[]> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            String[] notificationArray = new String[3];
            String id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX);
            String notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String notificationUri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);
            if (isFirst){
                if (sharedPreference.getStoreNotificationRingtoneURI(getActivity().getApplicationContext()).equals("Default ringtone")){
                    sharedPreference.storeNotificationRingtoneURI(getActivity().getApplicationContext(),notificationUri+"/"+id);
                }
                isFirst = false;
            }
            notificationArray[NOTIFICATION_TITLE] = notificationTitle;
            notificationArray[NOTIFICATION_URI] = notificationUri;
            notificationArray[NOTIFICATION_ID] = id;
            list.add(notificationArray);
            Log.d("Notification", notificationTitle);
            Log.d("Notification URI", notificationUri);
        }
        return list;
    }

    private void initUI(View rootView) {
        notificationListView = (ListView) rootView.findViewById(R.id.listview_notification);
        notificationModelArrayList = new ArrayList<>();
        Context context = getActivity().getApplicationContext();
        NotificationModel notificationModel = new NotificationModel();
        notificationModel = new NotificationModel();
        notificationModel.setNotificationItemLabel("Notification tone");
        notificationModel.setNotificationSubItemLabel(sharedPreference.getStoreNotificationRingTone(context));
        notificationModelArrayList.add(notificationModel);
        notificationModel = new NotificationModel();
        notificationModel.setNotificationItemLabel("Vibrate");
        notificationModel.setNotificationSubItemLabel(sharedPreference.getStoreNotificationSound(context));
        notificationModelArrayList.add(notificationModel);
        notificationModel = new NotificationModel();
        notificationModel.setNotificationItemLabel("Popup notification");
        notificationModel.setNotificationSubItemLabel(sharedPreference.getStoreNotificationPopUp(context));
        notificationModelArrayList.add(notificationModel);

        conversationCheckBox = (CheckBox)rootView.findViewById(R.id.checkbox_conversation);
        conversationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                sharedPreference.storeConversationTones(getActivity().getApplicationContext(), isChecked);
            }
        });
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }



}
