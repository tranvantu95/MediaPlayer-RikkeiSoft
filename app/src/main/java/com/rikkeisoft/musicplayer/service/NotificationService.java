package com.rikkeisoft.musicplayer.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.rikkeisoft.musicplayer.player.NotificationPlayer;

public class NotificationService extends Service {

    public static final int NOTIFICATION_ID = 2310;

    private static final String DATA = "player_data";
    private static final String IS_SHOWING_NOTIFICATION_KEY = "isShowingNotification";

    // binder
    private final IBinder mBinder = new NotificationService.LocalBinder();

    public class LocalBinder extends Binder {
        public NotificationService getService() {
            return NotificationService.this;
        }
    }

    private SharedPreferences preferences;

    public boolean isShowingNotification;

    private NotificationPlayer notificationPlayer;

    public NotificationPlayer getNotificationPlayer() {
        return notificationPlayer;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("debug", "onCreate " + getClass().getSimpleName());

        preferences = getApplicationContext().getSharedPreferences(DATA, Context.MODE_PRIVATE);

        isShowingNotification = preferences.getBoolean(IS_SHOWING_NOTIFICATION_KEY, false);
        isShowingNotification = true; // test

        notificationPlayer = new NotificationPlayer(this);

    }

    public void showNotification(boolean startForeground) {
        Log.d("debug", "showNotification " + startForeground + " " + getClass().getSimpleName());
        if(!isShowingNotification) {
            isShowingNotification = true;
            saveIsShowingNotification();
        }

        if(startForeground)
            startForeground(NOTIFICATION_ID, notificationPlayer.getNotification());
        else {
            stopForeground(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP);
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notificationPlayer.getNotification());
        }
    }

    public void deleteNotification(){
        Log.d("debug", "deleteNotification " + getClass().getSimpleName());
        onDeleteNotification();

        stopForeground(true);
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
    }

    public void onDeleteNotification() {
        Log.d("debug", "onDeleteNotification " + getClass().getSimpleName());
        if(isShowingNotification) {
            isShowingNotification = false;
            saveIsShowingNotification();
        }
    }

    public boolean isShowingNotification() {
        if(!isShowingNotification) return false;

        // notificationPlayer can hidden by user settings
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager nm = (NotificationManager)
                    getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            if(nm != null) {
                StatusBarNotification[] activeNotifications = nm.getActiveNotifications();

                for (StatusBarNotification notification : activeNotifications)
                    if(notification.getId() == NOTIFICATION_ID) return true;

                return false;
            }
        }

        return true;
    }

    private void saveIsShowingNotification() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_SHOWING_NOTIFICATION_KEY, isShowingNotification);
        editor.apply();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("debug", "onStartCommand " + getClass().getSimpleName());

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("debug", "onBind " + getClass().getSimpleName());

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("debug", "onUnbind " + getClass().getSimpleName());
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d("debug", "onRebind " + getClass().getSimpleName());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("debug", "onTaskRemoved " + getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("debug", "onDestroy " + getClass().getSimpleName());

        deleteNotification();
    }

}
