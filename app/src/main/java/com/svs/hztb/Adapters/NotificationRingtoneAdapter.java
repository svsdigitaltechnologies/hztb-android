package com.svs.hztb.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.svs.hztb.R;

import java.util.ArrayList;

/**
 * Created by VenuNalla on 10/11/15.
 */
public class NotificationRingtoneAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> notificationRingtoneList;
    private int selectedPosition;

    public NotificationRingtoneAdapter(Context context, ArrayList<String> ringtonesList) {
        this.mContext = context;
        this.notificationRingtoneList = ringtonesList;
    }

    @Override
    public int getCount() {
        return notificationRingtoneList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.layout_notification_ringtone_item, null);
        }
        if (selectedPosition == position) {
            ImageView radioButtonImage = (ImageView) view.findViewById(R.id.notification_ringtone_radiobutton);
            radioButtonImage.setImageResource(mContext.getResources().getIdentifier("selected_radio_button", "mipmap", mContext.getPackageName()));
        } else {
            ImageView radioButtonImage = (ImageView) view.findViewById(R.id.notification_ringtone_radiobutton);
            radioButtonImage.setImageResource(mContext.getResources().getIdentifier("unselected_radiobutton", "mipmap", mContext.getPackageName()));
        }
        TextView notificationRingtoneText = (TextView) view.findViewById(R.id.notification_ringtone_item);
        notificationRingtoneText.setText(notificationRingtoneList.get(position));
        return view;
    }


    public void setSelectedIndex(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

}
