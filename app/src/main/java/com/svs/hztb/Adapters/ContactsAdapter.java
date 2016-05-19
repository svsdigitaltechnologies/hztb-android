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
import android.widget.ImageView;
import android.widget.TextView;

import com.svs.hztb.Bean.Contact;
import com.svs.hztb.Bean.CountryItem;
import com.svs.hztb.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by venunalla on 18/05/16.
 */
public class ContactsAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Contact> contactArrayList;
    private ArrayList<Contact> contactss;


    public ContactsAdapter(Context context, ArrayList<Contact> contacts){
        this.mContext = context;
        this.contactArrayList = contacts;
        this.contactss = new ArrayList<>();
        this.contactss.addAll(contacts);
    }
    @Override
    public int getCount() {
        return contactArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return contactArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater mInflater = (LayoutInflater)
                mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.custom_contact_item, null);

        ImageView tick = (ImageView) convertView.findViewById(R.id.imageview_tick);
        if (contactArrayList.get(position).isSelected()) {
            tick.setVisibility(View.VISIBLE);
        } else tick.setVisibility(View.GONE);
        TextView contactName = (TextView) convertView.findViewById(R.id.textview_select_contacts);
        contactName.setText(contactArrayList.get(position).getContactName());
        ImageView contactImage = (ImageView)convertView.findViewById(R.id.contact_image);
        String contactPath = contactArrayList.get(position).getContactImagePath();
        if (contactPath != null) {
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(contactPath));
                contactImage.setImageBitmap(bitmap);
                System.out.println(bitmap);
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return convertView;
    }

    //
    // Filter Class
    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        contactArrayList.clear();
        if (charText.length() == 0) {
            contactArrayList.addAll(contactss);
        }
        else
        {
            for (Contact item : contactss)
            {
                if (item.getContactName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    contactArrayList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

}
