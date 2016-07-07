package com.svs.hztb.Application;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.nio.channels.FileChannel;

import io.realm.Realm;
import io.realm.RealmConfiguration;


/**
 * Created by VenuNalla on 7/6/16.
 */
public class HztbApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name("HZTB.realm")
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        String realmPath = new File(getApplicationContext().getFilesDir(), "HZTB.realm").getAbsolutePath();
        Log.d("PATH",realmPath);
    }


}

