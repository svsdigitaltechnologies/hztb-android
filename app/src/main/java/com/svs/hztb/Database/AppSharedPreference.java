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
    public static String NOTIFICATION_CONVERSATION = "notification_conversation";


    public static String NOTIFICATION_RINGTONE = "notification_ringtone";
    public static String NOTIFICATION_RINGTONE_POSITION = "notification_ringtone_position";
    public static String NOTIFICATION_RINGTONE_URI = "notification_ringtone_uri";

    public static String NOTIFICATION_SOUND_MODE = "notification_vibrate";
    public static String NOTIFICATION_SOUND_MODE_POSITION = "notification_vibrate_position";
    public static String NOTIFICATION_POPUP_MODE = "notification_popup";
    public static String NOTIFICATION_POPUP_MODE_POSITION = "notification_popup_position";


    public void storeNotificationRingtoneURI(Context context, String uri) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(NOTIFICATION_RINGTONE_URI, uri).commit();

    }

    public String getStoreNotificationRingtoneURI(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(NOTIFICATION_RINGTONE_URI, "Default ringtone");
    }

    public void storeConversationTones(Context context,boolean isChecked){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(NOTIFICATION_CONVERSATION, isChecked).commit();
    }

    public boolean getStoredConversationTones(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(NOTIFICATION_CONVERSATION, false);
    }

    public void storeNotificationRingToneposition(Context context, int position) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(NOTIFICATION_RINGTONE_POSITION, position).commit();
    }

    public void storeNotificationSoundposition(Context context, int position) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(NOTIFICATION_SOUND_MODE_POSITION, position).commit();

    }

    public void storeNotificationPopUpposition(Context context, int position) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(NOTIFICATION_POPUP_MODE_POSITION, position).commit();
    }


    public void storeNotificationRingTone(Context context, String name) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(NOTIFICATION_RINGTONE, name).commit();

    }

    public void storeNotificationSound(Context context, String name) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(NOTIFICATION_SOUND_MODE, name).commit();

    }

    public void storeNotificationPopUp(Context context, String name) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(NOTIFICATION_POPUP_MODE, name).commit();
    }

    public int getStoreNotificationRingToneposition(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(NOTIFICATION_RINGTONE_POSITION, 0);

    }

    public int getStoreNotificationSoundposition(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(NOTIFICATION_SOUND_MODE_POSITION, 1);

    }

    public int getStoreNotificationPopUpposition(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(NOTIFICATION_POPUP_MODE_POSITION, 3);
    }

    public String getStoreNotificationRingTone(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(NOTIFICATION_RINGTONE, "Default ringtone");

    }

    public String getStoreNotificationSound(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(NOTIFICATION_SOUND_MODE, "Default");

    }

    public String getStoreNotificationPopUp(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(NOTIFICATION_POPUP_MODE, "Always show popup");
    }


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
