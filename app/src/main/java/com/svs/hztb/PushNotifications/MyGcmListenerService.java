package com.svs.hztb.PushNotifications;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.support.v4.app.NotificationCompat;
import com.google.android.gms.gcm.GcmListenerService;
import com.svs.hztb.Activities.HomeScreenActivity;
import com.svs.hztb.Activities.PopupViewActivity;
import com.svs.hztb.Activities.ProfileActivity;
import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.R;

import java.util.Timer;
import java.util.TimerTask;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    private  final int VIBRATE_OFF = 0;
    private  final int VIBRATE_DEFAULT = 1;
    private  final int VIBRATE_SHORT = 2;
    private  final int POPUP_OFF = 0;
    private  final int POPUP_ON_WHEN_SCREEN_ON = 1;
    private  final int POPUP_ON_WHEN_SCREEN_OFF = 2;


    private boolean isConversationTonesEnabled;
    private String getNotificationRingtoneUri;
    private int vibrateType;
    private int popUpType;
    private String message;
    private Uri notificationSoundURI;
    private boolean noVibrate;
    private long[] vibrateMode;
    private long[] vibrateLong = new long[] { 1000, 1000, 1000, 1000, 1000,1000,1000 };
    private long[] vibrateShort = new long[] { 1000, 1000};
    private long[] vibrateDefault = new long[] { 1000, 1000, 1000, 1000 };
    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        getDefaultValuesFromSharedPreference(this);
        boolean screenLocked = getDisplayStatus(this);

        if (getNotificationRingtoneUri.equals("Default ringtone"))
        {
            // define sound URI, the sound to be played when there's a notification
            notificationSoundURI = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }else {
            notificationSoundURI = Uri.parse(getNotificationRingtoneUri);
        }

        sendNotification(message);
     }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {

        Intent intent = new Intent(this, HomeScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (message.contains("Opinion Request")){
            intent.putExtra("request",true);
        }else if (message.contains("Opinion response")){
            intent.putExtra("response",true);
        }
        intent.putExtra("true","Excell");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Notification notification;
        notification = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("HowzthisBuddy")
                    .setContentIntent(pendingIntent)
                    .setSound(notificationSoundURI)
                    .setContentText(message)
                    .setPriority(Notification.PRIORITY_HIGH).build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //manager.notify(R.string.app_name, notification);
        manager.notify(0, notification);
        {
            // Wake Android Device when notification received
            PowerManager pm = (PowerManager) getApplicationContext()
                    .getSystemService(Context.POWER_SERVICE);
            final PowerManager.WakeLock mWakelock = pm.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK
                            | PowerManager.ACQUIRE_CAUSES_WAKEUP, "GCM_PUSH");
            mWakelock.acquire();

            // Timer before putting Android Device to sleep mode.
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                public void run() {
                    mWakelock.release();
                }
            };
            timer.schedule(task, 5000);
        }
    }


    private void getDefaultValuesFromSharedPreference(Context context) {
        AppSharedPreference sharedPreferenceHelper = new AppSharedPreference();
        isConversationTonesEnabled = sharedPreferenceHelper.getStoredConversationTones(context);
        getNotificationRingtoneUri = sharedPreferenceHelper.getStoreNotificationRingtoneURI(context);
        vibrateType = sharedPreferenceHelper.getStoreNotificationSoundposition(context);
        popUpType = sharedPreferenceHelper.getStoreNotificationPopUpposition(context);
        Log.d("Nme", "Na");
    }

    private boolean getDisplayStatus(Context context) {
        boolean isLocked = false;
        KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if( myKM.inKeyguardRestrictedInputMode()) {
            isLocked = true;
        } else {
            isLocked = false;
        }
        return isLocked;
    }


}