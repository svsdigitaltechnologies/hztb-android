package com.svs.hztb.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.svs.hztb.R;

/**
 * Created by venunalla on 17/05/16.
 */
public class RetriveGroupsAdapter extends BaseAdapter {
    private Context mContext;
    private String groups[];

    public RetriveGroupsAdapter(Context context,String[] group){
        this.mContext = context;
        this.groups = group;
    }

    @Override
    public int getCount() {
        return groups.length;
    }

    @Override
    public Object getItem(int i) {
        return groups[i];
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

        if (groups[position].length()-1 == position){

        }else {

        }

        return convertView;
    }
}
