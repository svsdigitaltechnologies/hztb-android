package com.svs.hztb.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.svs.hztb.R;

import java.util.List;

/**
 * Created by VenuNalla on 5/5/16.
 */
public class SpinnerAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private LayoutInflater inflater;
    private String[] countries;
    public SpinnerAdapter(Context context, int textViewResourceId,
                           String[] countris) {
        super(context, textViewResourceId, countris);
        // TODO Auto-generated constructor stub
        mContext = context;
        countries = countris;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        //return super.getView(position, convertView, parent);

        inflater = LayoutInflater.from(mContext);
        View row=inflater.inflate(R.layout.custom_spinner_item, parent, false);
        Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(),  "fonts/walkway_ultrabold.ttf");
        TextView label=(TextView)row.findViewById(R.id.custom_spinner_label);
        label.setText(countries[position]);
        label.setTypeface(custom_font);
        return row;
    }
}

