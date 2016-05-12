package com.svs.hztb.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.svs.hztb.R;

/**
 * Created by venunalla on 12/05/16.
 */
public class SlideMenuAdapter extends BaseAdapter {

    private Context mContext;
    private String[] menuItems;

    public SlideMenuAdapter(Context context, String[] items){
        this.mContext = context;
        this.menuItems = items;
    }


    @Override
    public int getCount() {
        return menuItems.length;
    }

    @Override
    public Object getItem(int i) {
        return menuItems[i];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater)
                mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(), "fonts/walkway_ultrabold.ttf");

        if (position == 0){
            convertView = mInflater.inflate(R.layout.drawer_image_thumb, null);
            TextView name = (TextView) (convertView).findViewById(R.id.thumb_image);
            name.setTypeface(custom_font);
        }
        else {
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
            TextView menuItem = (TextView) (convertView).findViewById(R.id.drawer_list_item_textview);
            menuItem.setText(menuItems[position]);
            menuItem.setTypeface(custom_font);
        }
        return convertView;
    }
}
