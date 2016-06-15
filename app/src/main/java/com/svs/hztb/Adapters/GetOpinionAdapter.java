package com.svs.hztb.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.svs.hztb.Bean.Contact;
import com.svs.hztb.Bean.OpinionData;
import com.svs.hztb.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by VenuNalla on 6/15/16.
 */
public class GetOpinionAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<OpinionData> opinionDatasArrayList;
    public GetOpinionAdapter(Context context, ArrayList<OpinionData> contacts){
        this.mContext = context;
        this.opinionDatasArrayList = contacts;
    }
    @Override
    public int getCount() {
        return opinionDatasArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return opinionDatasArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater mInflater = (LayoutInflater)
                mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.custom_get_opinions_item, null);
        TextView productName = (TextView)convertView.findViewById(R.id.textView2);
        TextView productID = (TextView)convertView.findViewById(R.id.textView3);
        TextView productDescription = (TextView)convertView.findViewById(R.id.product_description);
        TextView productPrice = (TextView)convertView.findViewById(R.id.product_price);
        TextView textViewdateOpinion = (TextView)convertView.findViewById(R.id.textview_date_getopinion);

        Button buttonOk = (Button)convertView.findViewById(R.id.button_double_ok);
        Button buttonSingleOk = (Button)convertView.findViewById(R.id.button_single_ok);
        Button buttonDown = (Button)convertView.findViewById(R.id.button_down);
        Button buttonMayBe = (Button)convertView.findViewById(R.id.button_maybe);
        Button viewSelf = (Button)convertView.findViewById(R.id.button_view_selfie);

        OpinionData opinionData = opinionDatasArrayList.get(position);
        productName.setText(opinionData.getProductName());
        productID.setText(String.valueOf(opinionData.getOpinionId()));

        return convertView;
    }

}
