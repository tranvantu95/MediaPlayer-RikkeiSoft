package com.rikkeisoft.musicplayer.app;

import android.Manifest;
import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.base.SwitchListModel;
import com.rikkeisoft.musicplayer.service.PlayerService;
import com.rikkeisoft.musicplayer.utils.General;
import com.rikkeisoft.musicplayer.utils.Loader;
import com.rikkeisoft.musicplayer.player.PlaylistPlayer;

public class MyApplication extends Application {

    // data saved
    public static final String DATA = "app_data";

    // Media change listener
    public static final String ACTION_MEDIA_CHANGE = "com.rikkeisoft.musicplayer.action.MEDIA_CHANGE";
    private ContentObserver onMediaChange;

    private void registerOnMediaChange() {
        if(onMediaChange != null) return;
        Log.d("debug", "registerOnMediaChange " + getClass().getSimpleName());

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
            Log.d("debug", "unregisterOnMediaChange " + getClass().getSimpleName());
            getContentResolver().unregisterContentObserver(onMediaChange);
            onMediaChange = null;
        }
    }

    private void onMediaChange() {
        Log.d("debug", "onMediaChange " + getClass().getSimpleName());
        Loader.getInstance().clearCache();
        sendBroadcastMediaChange();
    }

    // broadcast for activity
    private void sendBroadcastMediaChange() {
        Log.d("debug", "sendBroadcastMediaChange " + getClass().getSimpleName());
        Intent intent = new Intent(ACTION_MEDIA_CHANGE);
        sendBroadcast(intent);
    }

    // cache for playerService
    private static PlayerService playerService;

    public static PlayerService getPlayerService() {
        return playerService;
    }

    public static void setPlayerService(PlayerService playerService) {
        MyApplication.playerService = playerService;
    }

    private static PlayerModel playerModel;

    public static PlayerModel getPlayerModel() {
        return playerModel;
    }

    public static void setPlayerModel(PlayerModel playerModel) {
        MyApplication.playerModel = playerModel;
    }

    private static PlaylistPlayer playlistPlayer;

    public static PlaylistPlayer getPlaylistPlayer() {
        return playlistPlayer;
    }

    public static void setPlaylistPlayer(PlaylistPlayer playlistPlayer) {
        MyApplication.playlistPlayer = playlistPlayer;
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
        General.isPermissionGranted = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

    }

}
