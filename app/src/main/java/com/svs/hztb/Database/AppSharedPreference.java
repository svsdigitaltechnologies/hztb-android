package com.svs.hztb.Database;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.svs.hztb.R;

import java.io.ByteArrayOutputStream;

/**
 * Created by VenuNalla on 5/15/16.
 */
public class AppSharedPreference  {

    private String kUserName = "USERNAME";
    private String kUserEmail = "USEREMAIL";
    private String kUserID = "USERID";
    private String kUserPicArray = "BYTEARRATY";



    private   void storeNameAndEmail (String name , String email, Context context){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(kUserName, name).commit();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(kUserEmail, email).commit();
    }

    public  void storeUserID (String userId,Context context){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(kUserID, userId).commit();
    }

    public String getUserID(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(kUserID, "");
    }


    public void storeBitmap(Context context, byte[] byteArray) {
        String imageData = Base64.encodeToString(byteArray, Base64.DEFAULT);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(kUserPicArray, imageData).commit();
    }

    public Bitmap getUserBitmap(Context context) {
        String encodedString = PreferenceManager.getDefaultSharedPreferences(context).getString(kUserPicArray, "");
        if (!encodedString.isEmpty() || encodedString != null) {
            byte[] picArray = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(picArray , 0, picArray .length);
        }else
            return BitmapFactory.decodeResource(context.getResources(), R.mipmap.thumb);
    }

    public String getUserName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(kUserName, "");
    }

    public void storeSuccessLoginInSharedPreferences(Context context,String username, String email,byte[] picArray) {
        SharedPreferences sharedpref = context.getSharedPreferences(context.getResources().getString(R.string.shared_pref_app), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putBoolean(context.getResources().getString(R.string.login_success),true);
        editor.commit();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(kUserName, username).commit();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(kUserEmail, email).commit();
        String imageData = Base64.encodeToString(picArray, Base64.DEFAULT);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(kUserPicArray, imageData).commit();

    }
}
