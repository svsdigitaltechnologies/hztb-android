package com.svs.hztb.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.svs.hztb.Bean.OpinionData;
import com.svs.hztb.Bean.OpinionResponseData;
import com.svs.hztb.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VenuNalla on 6/24/16.
 */
public class GetDetailOpinionAdapter  extends BaseAdapter {
    private Context mContext;
    private List<OpinionResponseData> opinionDatasArrayList;

    public GetDetailOpinionAdapter(Context context, List<OpinionResponseData> opinionResponseDatas) {
        this.mContext = context;
        this.opinionDatasArrayList = opinionResponseDatas;
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
        convertView = mInflater.inflate(R.layout.custom_opinion_detail_response_item, null);

        ViewHolder holder = new ViewHolder();
        final OpinionResponseData opinionData = opinionDatasArrayList.get(position);

        holder.userIdName = (TextView) convertView.findViewById(R.id.textview_name_detail);
        holder.thumbImage = (Button) convertView.findViewById(R.id.thumb_rating);
        holder.thumbImage.setText(opinionData.getResponseType());
        holder.comment = (Button) convertView.findViewById(R.id.comment_button);
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,opinionData.getResponseText(),Toast.LENGTH_LONG).show();
            }
        });
        holder.userIdName.setText("User Id :"+opinionData.getResponderUserId());
        convertView.setTag(holder);
        return convertView;
    }

    class ViewHolder {
        TextView userIdName;
        Button thumbImage;
        Button comment;
    }

}

