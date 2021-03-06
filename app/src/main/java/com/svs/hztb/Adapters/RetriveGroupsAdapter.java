package com.svs.hztb.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.svs.hztb.Bean.ContactGroup;
import com.svs.hztb.Bean.GroupDetail;
import com.svs.hztb.R;

import java.util.ArrayList;

/**
 * Created by venunalla on 17/05/16.
 */
public class RetriveGroupsAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<GroupDetail> groupList;
    private boolean isGroupScreen;
    public RetriveGroupsAdapter(Context context,ArrayList<GroupDetail> group,boolean isGroupScren){
        this.mContext = context;
        this.groupList = group;
        this.isGroupScreen = isGroupScren;
    }

    @Override
    public int getCount() {
        return groupList.size();
    }

    @Override
    public Object getItem(int i) {
        return groupList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        LayoutInflater mInflater = (LayoutInflater)
                mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.custom_contacts_item_layout, null);
            TextView groupName = (TextView) (convertView).findViewById(R.id.textview_select_contacts);
            groupName.setText(groupList.get(position).getGroupName());
        if (isGroupScreen){
            groupName.setTextColor(mContext.getResources().getColor(R.color.picton_blue));
            ImageView rightArrow = (ImageView) (convertView).findViewById(R.id.imageView2);
            rightArrow.setVisibility(View.VISIBLE);

        }else {
            if (position == groupList.size()-1){
                groupName.setTextColor(mContext.getResources().getColor(R.color.picton_blue));
                ImageView rightArrow = (ImageView) (convertView).findViewById(R.id.imageView2);
                rightArrow.setVisibility(View.VISIBLE);

            }else {
                ImageView tickImage = (ImageView) (convertView).findViewById(R.id.imageView_selected);
                if (groupList.get(position).isSelect()){
                    tickImage.setVisibility(View.VISIBLE);
                }else tickImage.setVisibility(View.GONE);
            }

        }

        return convertView;
    }
}
