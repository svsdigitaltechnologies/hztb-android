package com.svs.hztb.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.svs.hztb.Bean.OpinionData;
import com.svs.hztb.Bean.OpinionResponseData;
import com.svs.hztb.Bean.UserData;
import com.svs.hztb.R;
import com.svs.hztb.RealmDatabase.RealmUserData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

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

        /*
        Realm realm = Realm.getDefaultInstance();

        RealmResults<RealmUserData> userList = realm.where(RealmUserData.class).findAll();
        Iterator<RealmUserData> userDataIterator = userList.iterator();
        while (userDataIterator.hasNext()){
            RealmUserData userData = userDataIterator.next();
            Log.d("userids",userData.toString());
        }


//        RealmUserData userData =  realm.where(RealmUserData.class).equalTo("userId", opinionData.getResponderUserId()).findFirst();
*/
        holder.userIdName.setText("UserID :"+opinionData.getResponderUserId());
        convertView.setTag(holder);
        return convertView;
    }

    class ViewHolder {
        TextView userIdName;
        Button thumbImage;
        Button comment;
    }

}

