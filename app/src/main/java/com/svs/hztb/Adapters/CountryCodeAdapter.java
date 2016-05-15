package com.svs.hztb.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.svs.hztb.Bean.CountryItem;
import com.svs.hztb.CustomViews.WalkWayTextView;
import com.svs.hztb.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by VenuNalla on 5/5/16.
 */
public class CountryCodeAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<CountryItem> countries;
    private ArrayList<CountryItem> countryList = null;

    public CountryCodeAdapter(Context context,
                              ArrayList<CountryItem> countryItemArrayList) {
        mContext = context;
        countries = countryItemArrayList;
        countryList = new ArrayList<>();
        countryList.addAll(countries);
    }

    @Override
    public int getCount() {
        return countries.size();
    }

    @Override
    public Object getItem(int position) {
        return countries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater)
                mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(R.layout.custom_countrycode_item, null);

        TextView label=(TextView) convertView.findViewById(R.id.custom_spinner_label);
        label.setText(countries.get(position).getCountryName());
        TextView code=(TextView)convertView.findViewById(R.id.code_name);
        code.setText( String.valueOf(mContext.getResources().getString(R.string.string_plus)+countries.get(position).getCountryCode()));
        return convertView;
    }

    //
    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        countries.clear();
        if (charText.length() == 0) {
            countries.addAll(countryList);
        }
        else
        {
            for (CountryItem item : countryList)
            {
                if (item.getCountryName().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    countries.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }


}

