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

        ViewHolder holder = new ViewHolder();

        holder.productName = (TextView)convertView.findViewById(R.id.textView2);
        holder.productID = (TextView)convertView.findViewById(R.id.textView3);
        holder.productDescription = (TextView)convertView.findViewById(R.id.product_description);
        holder.productPrice = (TextView)convertView.findViewById(R.id.product_price);
        holder.textViewdateOpinion = (TextView)convertView.findViewById(R.id.textview_date_getopinion);

        holder.buttonOk = (Button)convertView.findViewById(R.id.button_double_ok);
        holder.buttonSingleOk = (Button)convertView.findViewById(R.id.button_single_ok);
        holder.buttonDown = (Button)convertView.findViewById(R.id.button_down);
        holder.buttonMayBe = (Button)convertView.findViewById(R.id.button_maybe);
        holder.viewSelf = (Button)convertView.findViewById(R.id.button_view_selfie);

        OpinionData opinionData = opinionDatasArrayList.get(position);
        holder.productName.setText(opinionData.getProductName());
        holder.productID.setText(String.valueOf(opinionData.getOpinionId()));
        convertView.setTag(holder);
        return convertView;
    }

    class  ViewHolder{
        TextView productName;
        TextView productID;
        TextView productDescription;
        TextView productPrice;
        TextView textViewdateOpinion;
        Button buttonOk;
        Button buttonSingleOk;
        Button buttonDown;
        Button buttonMayBe;
        Button viewSelf;
    }

}
