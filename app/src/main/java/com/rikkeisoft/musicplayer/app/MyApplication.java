package com.rikkeisoft.musicplayer.app;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.rikkeisoft.musicplayer.model.base.SwitchListModel;
import com.rikkeisoft.musicplayer.utils.General;
import com.rikkeisoft.musicplayer.utils.Loader;

public class MyApplication extends Application {

    // data saved
    public static final String DATA = "app_data";

    // Media change listener
    public static final String ACTION_MEDIA_CHANGE = "com.rikkeisoft.musicplayer.action.MEDIA_CHANGE";
    private ContentObserver onMediaChange;

    private void registerOnMediaChange() {
        if(onMediaChange != null) return;

        onMediaChange = new ContentObserver(new Handler()) {
            private long latestTime = System.currentTimeMillis();

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);

                if(System.currentTimeMillis() - latestTime > 1000) {
                    latestTime = System.currentTimeMillis();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onMediaChange();
                        }
                    }, 1000);
                }

            }
        };

        getContentResolver().registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, onMediaChange);
    }

    private void unregisterOnMediaChange() {
        if(onMediaChange != null) {
            getContentResolver().unregisterContentObserver(onMediaChange);
            onMediaChange = null;
        }
    }

    private void onMediaChange() {
        Log.d("debug", "onMediaChange " + getClass().getSimpleName());
        Loader.getInstance().clearCache();
        sendBroadcastMediaChange();
    }

    private void sendBroadcastMediaChange() {
        Intent intent = new Intent(ACTION_MEDIA_CHANGE);
        sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("debug", "onCreate " + getClass().getSimpleName());

        // use vector drawable library
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        // initialize media loader
        Loader.initialize(this);

        // listen media change
        registerOnMediaChange();

        // get data at application level
        SharedPreferences sharedPreferences = getSharedPreferences(DATA, MODE_PRIVATE);
        General.shouldShowRequestPermissionRationale = sharedPreferences.getBoolean(
                "shouldShowRequestPermissionRationale", false);
        General.typeView = sharedPreferences.getInt("typeView", SwitchListModel.LIST);

    }

}
