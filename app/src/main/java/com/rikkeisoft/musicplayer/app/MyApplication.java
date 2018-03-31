package com.rikkeisoft.musicplayer.app;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.base.SwitchListModel;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.service.PlayerService;
import com.rikkeisoft.musicplayer.utils.General;
import com.rikkeisoft.musicplayer.utils.Loader;
import com.rikkeisoft.musicplayer.utils.PlaylistPlayer;

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

    //
    private static PlayerModel playerModel;

    public static PlayerModel getPlayerModel() {
        return playerModel;
    }

    private static PlayerService playerService;

    public static PlayerService getPlayerService() {
        return playerService;
    }

    private static PlaylistPlayer playlistPlayer;

    public static PlaylistPlayer getPlaylistPlayer() {
        return playlistPlayer;
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

        //
        playerModel = new PlayerModel();

        //
        Intent intent = new Intent(this, PlayerService.class);

        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                PlayerService.LocalBinder binder = (PlayerService.LocalBinder) iBinder;
                playerService = binder.getService();
                playlistPlayer = playerService.getPlaylistPlayer();

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };

        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

//        try {
//            Thread.sleep(3000);
//        }catch (Exception e) {
//
//        }

//        startService(intent);
//        stopService(intent);
    }

}
