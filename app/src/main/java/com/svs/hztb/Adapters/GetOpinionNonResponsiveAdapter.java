package com.svs.hztb.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.Interfaces.IDrawerClosed;
import com.svs.hztb.R;

import java.util.List;

/**
 * Created by VenuNalla on 6/24/16.
 */
public class GetOpinionNonResponsiveAdapter extends BaseAdapter {

    private Context mContext;
    private List<Integer> userIDS;
    public GetOpinionNonResponsiveAdapter(Context context, List<Integer> ids){
        this.mContext = context;
        this.userIDS = ids;
    }

    @Override
    public int getCount() {
        return userIDS.size();
    }

    @Override
    public Object getItem(int i) {
        return userIDS.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater)
                mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
            TextView textView = (TextView) (convertView).findViewById(R.id.drawer_list_item_textview);
             textView.setText("User ID"+userIDS.get(position));
        textView.setTextColor(ContextCompat.getColor(mContext, android.R.color.black));
        return convertView;
    }
}
