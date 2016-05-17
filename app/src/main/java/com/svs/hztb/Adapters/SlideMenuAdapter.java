package com.svs.hztb.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.svs.hztb.Activities.AbstractActivity;
import com.svs.hztb.Activities.HomeScreenActivity;
import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.Interfaces.IDrawerClosed;
import com.svs.hztb.R;

/**
 * Created by venunalla on 12/05/16.
 */
public class SlideMenuAdapter extends BaseAdapter {

    private Context mContext;
    private String[] menuItems;
    private IDrawerClosed drawerClosed;
    public SlideMenuAdapter(Context context, String[] items,IDrawerClosed iDrawerClosed){
        this.mContext = context;
        this.menuItems = items;
        drawerClosed = iDrawerClosed;
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

        if (position == 0){
            convertView = mInflater.inflate(R.layout.drawer_image_thumb, null);
            TextView name = (TextView) (convertView).findViewById(R.id.thumb_image);
            name.setText(new AppSharedPreference().getUserName(mContext));
            Bitmap picBitmap = new AppSharedPreference().getUserBitmap(mContext);
            ImageView profileImage= (ImageView) (convertView).findViewById(R.id.drawer_layout_thumbImage);
            profileImage.setImageBitmap(picBitmap);
            Button closeDrawer = (Button)(convertView).findViewById(R.id.button);
            closeDrawer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        drawerClosed.onDrawerClosedClicked();
                }
            });

        }
        else {
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
            TextView menuItem = (TextView) (convertView).findViewById(R.id.drawer_list_item_textview);
            menuItem.setText(menuItems[position]);
        }
        return convertView;
    }
}
