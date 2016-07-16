package com.svs.hztb.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.svs.hztb.Bean.GivenPendingData;
import com.svs.hztb.Bean.OpinionData;
import com.svs.hztb.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VenuNalla on 6/24/16.
 */
public class OpinionDetailGivenAdapter extends BaseAdapter {
    private Context mContext;
    private List<GivenPendingData> opinionGivenPendingArrayList;
    public OpinionDetailGivenAdapter(Context context, List<GivenPendingData> contacts){
        this.mContext = context;
        this.opinionGivenPendingArrayList = contacts;
    }
    @Override
    public int getCount() {
        return opinionGivenPendingArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return opinionGivenPendingArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater mInflater = (LayoutInflater)
                mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.custom_product_without_description, null);

        ViewHolder holder = new ViewHolder();

        holder.productName = (TextView)convertView.findViewById(R.id.textView2);
        holder.productID = (TextView)convertView.findViewById(R.id.textView3);
        holder.productPrice = (TextView)convertView.findViewById(R.id.product_price);
        holder.textViewdateOpinion = (TextView)convertView.findViewById(R.id.textview_date_getopinion);

        holder.buttonOk = (ImageView)convertView.findViewById(R.id.button_double_ok);
        holder.buttonSingleOk = (ImageView)convertView.findViewById(R.id.button_single_ok);
        holder.buttonDown = (ImageView)convertView.findViewById(R.id.button_down);
        holder.buttonMayBe = (ImageView)convertView.findViewById(R.id.button_maybe);
        holder.viewSelf = (Button)convertView.findViewById(R.id.button_view_selfie);


        GivenPendingData givenPendingData = opinionGivenPendingArrayList.get(position);
        if (givenPendingData.getResponseText() != null){
            hideButtons(holder);
            holder.buttonDown.setVisibility(View.VISIBLE);

            Drawable res=null;

            if (givenPendingData.getResponseType().equals("W")){
                res = ResourcesCompat.getDrawable(mContext.getResources(), R.mipmap.wow, null);
            }
            if (givenPendingData.getResponseType().equals("L")){
                res = ResourcesCompat.getDrawable(mContext.getResources(), R.mipmap.like, null);

            }
            if (givenPendingData.getResponseType().equals("N")){
                res = ResourcesCompat.getDrawable(mContext.getResources(), R.mipmap.neutral, null);

            }
            if (givenPendingData.getResponseType().equals("D")){
                res = ResourcesCompat.getDrawable(mContext.getResources(), R.mipmap.dont_like, null);
            }
            holder.buttonDown.setImageDrawable(res);
        }

        holder.productName.setText(givenPendingData.getProduct().getName());
        holder.productPrice.setText("Price : $"+String.valueOf(givenPendingData.getProduct().getPrice()));
        holder.productPrice.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        convertView.setTag(holder);
        return convertView;
    }

    private void hideButtons(ViewHolder holder) {
        holder.buttonDown.setVisibility(View.INVISIBLE);
        holder.buttonSingleOk.setVisibility(View.INVISIBLE);
        holder.buttonMayBe.setVisibility(View.INVISIBLE);
        holder.buttonOk.setVisibility(View.INVISIBLE);
    }

    class  ViewHolder{
        TextView productName;
        TextView productID;
        TextView productPrice;
        TextView textViewdateOpinion;
        ImageView buttonOk;
        ImageView buttonSingleOk;
        ImageView buttonDown;
        ImageView buttonMayBe;
        Button viewSelf;
    }
}
