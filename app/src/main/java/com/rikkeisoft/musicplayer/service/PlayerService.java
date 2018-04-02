package com.rikkeisoft.musicplayer.service;

import android.app.Service;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.rikkeisoft.musicplayer.app.MyApplication;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.utils.PlaylistPlayer;

public class PlayerService extends Service {

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

//    private class PlayerHandler extends Handler {
//        public PlayerHandler(Looper looper) {
//            super(looper);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            Bundle bundle = msg.getData();
//        }
//    }

//    private HandlerThread playerThread;
//    private Handler playerHandler;
//    private Handler uiHandler;

    private PlaylistPlayer playlistPlayer;

    public PlaylistPlayer getPlaylistPlayer() {
        return playlistPlayer;
    }

    private PlayerModel playerModel;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("debug", "onCreate " + getClass().getSimpleName());

//        playerThread = new HandlerThread("playerThread");
//        playerThread.start();
//
//        playerHandler = new PlayerHandler(playerThread.getLooper());
//        uiHandler = new Handler();

        playlistPlayer = new PlaylistPlayer(getApplicationContext(), MyApplication.getPlayerModel());
        playerModel = playlistPlayer.getPlayerModel();
        MyApplication.setPlayerModel(playerModel);


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("debug", "onStartCommand " + getClass().getSimpleName());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("debug", "onBind " + getClass().getSimpleName());
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("debug", "onUnbind " + getClass().getSimpleName());
        if(!playlistPlayer.isRunning()) {
            Log.d("debug", "stopSelf " + getClass().getSimpleName());
            stopSelf();
        }

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

        playlistPlayer.release();
        playlistPlayer = null;
    }

}
