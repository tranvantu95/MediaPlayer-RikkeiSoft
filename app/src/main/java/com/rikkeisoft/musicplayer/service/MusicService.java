package com.rikkeisoft.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.utils.MusicPlayer;
import com.rikkeisoft.musicplayer.utils.MusicPlayerInterface;

import java.io.FileDescriptor;

public class MusicService extends Service {

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    private MusicPlayer musicPlayer;

    public MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }

    private PlayerModel playerModel;

    public void setPlayerModel(PlayerModel playerModel) {
        this.playerModel = playerModel;
    }

    private HandlerThread musicThread;
    private Handler musicHandler;
    private Handler uiHandler;

    private class MusicHandler extends Handler {
        public MusicHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("debug", "onCreate " + getClass().getSimpleName());

        musicThread = new HandlerThread("musicThread");
        musicThread.start();

        musicHandler = new Handler(musicThread.getLooper());
        uiHandler = new Handler();

        musicPlayer = new MusicPlayer(new MusicPlayer.Callback() {
            @Override
            public void onPrepared(MusicPlayer musicPlayer) {
                playerModel.getDuration().setValue(musicPlayer.getDuration());
            }

            @Override
            public void onPause(MusicPlayer musicPlayer) {
                playerModel.getPaused().setValue(true);
            }

            @Override
            public void onPlay(MusicPlayer musicPlayer) {
                playerModel.getPaused().setValue(false);
            }

            @Override
            public void onToggleRepeat(MusicPlayer musicPlayer) {
                playerModel.getRepeat().setValue(musicPlayer.getRepeat());
            }

            @Override
            public void onUpdateTime(MusicPlayer musicPlayer) {
                playerModel.getCurrentTime().setValue(musicPlayer.getCurrentTime());
            }
        });

    }

//    @Override
//    public void prepare(int index) {
//
//    }
//
//    @Override
//    public void play(final int index) {
//        musicHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                musicPlayer.play(index);
//            }
//        });
//    }
//
//    @Override
//    public void start() {
//        musicHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                musicPlayer.start();
//            }
//        });
//    }
//
//    @Override
//    public void pause() {
//        musicHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                musicPlayer.pause();
//            }
//        });
//    }
//
//    @Override
//    public void resume() {
//        musicHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                musicPlayer.resume();
//            }
//        });
//    }
//
//    @Override
//    public void replay() {
//        musicHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                musicPlayer.replay();
//            }
//        });
//    }
//
//    @Override
//    public void previous() {
//        musicHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                musicPlayer.previous();
//            }
//        });
//    }
//
//    @Override
//    public void next() {
//        musicHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                musicPlayer.next();
//            }
//        });
//    }
//
//    @Override
//    public void setShuffle(boolean shuffle) {
//        musicHandler.post(new Runnable() {
//            @Override
//            public void run() {
//            }
//        });
//    }
//
//    @Override
//    public void setRepeat(int repeat) {
//        musicHandler.post(new Runnable() {
//            @Override
//            public void run() {
//            }
//        });
//    }
//
//    @Override
//    public void seekTo(final int ms) {
//        musicHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                musicPlayer.seekTo(ms);
//            }
//        });
//    }
//
//    @Override
//    public boolean isPlaying() {
//        return musicPlayer.isPlaying();
//    }
//
//    @Override
//    public boolean isRunning() {
//        return musicPlayer.isRunning();
//    }
//
//    @Override
//    public int getDuration() {
////        Message message = musicHandler.obtainMessage();
////        Bundle bundle = new Bundle();
////        bundle.putInt("get", 1);
////        message.setData(bundle);
//        return musicPlayer.getDuration();
//    }
//
//    @Override
//    public int getCurrentTime() {
//        return musicPlayer.getCurrentTime();
//    }
//
//    @Override
//    public void onUpdateTime(int ms) {
//
//    }
//
//    @Override
//    public void onPlay(int index) {
//
//    }
//
//    @Override
//    public void onPause(int index) {
//
//    }
//
//    @Override
//    public void onCompleteSong(int index) {
//
//    }
//
//    @Override
//    public void onCompletePlaylist(int index) {
//
//    }

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
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d("debug", "onRebind " + getClass().getSimpleName());
        super.onRebind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("debug", "onTaskRemoved " + getClass().getSimpleName());
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        Log.d("debug", "onDestroy " + getClass().getSimpleName());
        super.onDestroy();
    }

}
