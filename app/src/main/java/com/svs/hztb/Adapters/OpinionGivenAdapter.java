package com.svs.hztb.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
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
import com.svs.hztb.Bean.OpinionCountData;
import com.svs.hztb.Bean.OpinionData;
import com.svs.hztb.R;
import com.svs.hztb.RealmDatabase.RealmUserData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by VenuNalla on 6/24/16.
 */
public class OpinionGivenAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<OpinionCountData> opinionGivenArrayList;
        public OpinionGivenAdapter(Context context, ArrayList<OpinionCountData> opinionCountDatas){
            this.mContext = context;
            this.opinionGivenArrayList = opinionCountDatas;
        }
        @Override
        public int getCount() {
            return opinionGivenArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return opinionGivenArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            LayoutInflater mInflater = (LayoutInflater)
                    mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.custom_opinion_given_item, null);

            ViewHolder holder = new ViewHolder();

            holder.userImage = (ImageView) convertView.findViewById(R.id.contact_thumb);
            holder.userName = (TextView)convertView.findViewById(R.id.opinion_given_username);
            holder.givenCount = (TextView)convertView.findViewById(R.id.opinion_given_count);
            holder.pendingCount = (TextView)convertView.findViewById(R.id.opinion_pending_count);

            OpinionCountData opinionData = opinionGivenArrayList.get(position);
            /*
            Realm realm = Realm.getDefaultInstance();
            RealmUserData userData =  realm.where(RealmUserData.class).equalTo("userId", opinionData.getUserId()).findFirst();
            */
            holder.userName.setText("UserID  :" +opinionData.getUserId());
            holder.givenCount.setText("Given :"+opinionData.getGivenCount());
            holder.pendingCount.setText("Pending :"+opinionData.getPendingCount());
            convertView.setTag(holder);
            return convertView;
        }

        class  ViewHolder{
            ImageView userImage;
            TextView userName;
            TextView givenCount;
            TextView pendingCount;

        }

    }


