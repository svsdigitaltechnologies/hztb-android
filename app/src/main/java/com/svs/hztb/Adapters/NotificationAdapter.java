package com.svs.hztb.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.svs.hztb.Bean.NotificationModel;
import com.svs.hztb.R;

import java.util.ArrayList;

/**
 * Created by VenuNalla on 10/11/15.
 */
public class NotificationAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {

    private ArrayList<NotificationModel> notificationModelArrayList;
    private Context mContext;
    private Typeface _typefaceRegular = null;




    public NotificationAdapter(Context context, ArrayList<NotificationModel> notificationModels) {
        this.notificationModelArrayList = notificationModels;
        this.mContext = context;
        Typeface walkwayFont = Typeface.createFromAsset(context.getAssets(),  "fonts/walkway_ultrabold.ttf");

        _typefaceRegular = walkwayFont;

    }


    @Override
    public int getCount() {
        return notificationModelArrayList.size();
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
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        Log.d("Status is Checked ", "" + b);
    }

    private static class ViewHolder {
        TextView notifciationItem, notificationSubItem;
        CheckBox notificationConversationBox;
    }

    ViewHolder holder = null;

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.layout_notification_listview_item, null);
        }
        holder = new ViewHolder();
        holder.notifciationItem = (TextView) view.findViewById(R.id.textView_notification_item);
        holder.notificationSubItem = (TextView) view.findViewById(R.id.textView_notification_subitem);
        holder.notifciationItem.setTypeface(_typefaceRegular);
        holder.notificationSubItem.setTypeface(_typefaceRegular);
        view.setTag(holder);

        NotificationModel notificationModel = notificationModelArrayList.get(position);

        holder.notifciationItem.setText(notificationModel.getNotificationItemLabel());
        holder.notificationSubItem.setText(notificationModel.getNotificationSubItemLabel());
        return view;
    }
}
