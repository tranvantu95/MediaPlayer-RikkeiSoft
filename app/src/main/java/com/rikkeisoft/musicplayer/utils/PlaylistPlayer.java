package com.rikkeisoft.musicplayer.utils;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.item.SongItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class PlaylistPlayer extends MediaPlayer {

    public static final int UN_REPEAT = 0;
    public static final int REPEAT_PLAYLIST = 1;
    public static final int REPEAT_SONG = 2;

    private Handler handler;
    private Runnable updateCurrentPosition;

    private int repeat;
    private boolean shuffle;

    private boolean preparing, ready;

    private int currentIndex;

    private PlayerModel playerModel;
    private List<SongItem> playlist = new ArrayList<>();

    public PlaylistPlayer(PlayerModel _playerModel) {
        super();

        this.playerModel = _playerModel;

        handler = new Handler();

        updateCurrentPosition = new Runnable() {
            @Override
            public void run() {
                playerModel.getCurrentPosition().setValue(getCurrentPosition());
                handler.postDelayed(this, 100);
            }
        };

        playerModel.getCurrentPosition().setValue(0);
        playerModel.getPlaying().setValue(false);

//        setOnErrorListener(new OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//                // prepareAsync with url
//                return false;
//            }
//        });

//        setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
//            @Override
//            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
//                // prepareAsync with url
//            }
//        });

        setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                setReady(true);
            }
        });

        setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                next();
            }
        });
    }

    public void setWakeMode(Context context) {
        setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
    }

    public void observe(LifecycleOwner owner) {
        playerModel.getItems().observe(owner, new Observer<List<SongItem>>() {
            @Override
            public void onChanged(@Nullable List<SongItem> songItems) {
                if(songItems != null) playlist = songItems;
            }
        });

        playerModel.getCurrentIndex().observe(owner, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer != null) currentIndex = integer;
            }
        });
    }

    private String getPath(int index) {
        return playlist.get(index).getPath();
    }

    private void setPlaying(boolean playing) {
        if(playing) startUpdateCurrentPosition();
        else stopUpdateCurrentPosition();
        playerModel.getPlaying().setValue(playing);
    }

    private void setReady(boolean ready) {
        this.ready = ready;
        preparing = false;
        if(!ready) {
            playerModel.getCurrentPosition().setValue(0);
            setPlaying(false);
        }
        else {
            playerModel.getDuration().setValue(getDuration());
            start();
        }
    }

    private void setCurrentIndex(int index) {
        currentIndex = index;
        playerModel.getCurrentIndex().setValue(currentIndex);
    }

    @Override
    public void release() {
        super.release();
        stopUpdateCurrentPosition();
    }

    @Override
    public void reset() {
        super.reset();
        setReady(false);
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        setReady(false);
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        setPlaying(false);
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        setPlaying(true);
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        super.prepareAsync();
        preparing = true;
    }

    @Override
    public void prepare() throws IOException, IllegalStateException {
        super.prepare();
        preparing = true;
    }

    @Override
    public void seekTo(int msec) throws IllegalStateException {
        super.seekTo(msec);
    }

    public void play(int index) {
        if(index < 0 || index >= playlist.size()) {
            Log.d("debug", "index out of bound " + getClass().getSimpleName());
            return;
        }

        if(preparing) return;

        if(ready && currentIndex == index) {
            if(!isPlaying()) start();
//            else seekTo(0);
            return;
        }

        setCurrentIndex(index);

        reset();

        try {
            setDataSource(getPath(index));
            prepare();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void next() {
        if(repeat == REPEAT_SONG) {
//            seekTo(0);
            return;
        }

        preparing = false;

        if(shuffle) {

        }
        else {
            if(currentIndex < playlist.size() - 1) {
                play(currentIndex + 1);
            }
            else if(repeat == REPEAT_PLAYLIST) {
                play(0);
            }
            else {
                setCurrentIndex(0);
                reset();
            }
        }
    }

    public void previous() {

    }

    public void togglePlay() {
        if(!ready) {
            if(currentIndex < 0 || currentIndex >= playlist.size()) currentIndex = 0;
            play(currentIndex);
        }
        else if(!isPlaying()) start();
        else pause();
    }

    public void toggleShuffle() {

    }

    public void toggleRepeat() {
        switch (repeat) {
            case UN_REPEAT:
                setRepeat(REPEAT_PLAYLIST);
                break;

            case REPEAT_PLAYLIST:
                setRepeat(REPEAT_SONG);
                break;

            case REPEAT_SONG:
                setRepeat(UN_REPEAT);
                break;
        }
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
        setLooping(repeat == REPEAT_SONG);
        playerModel.getRepeat().setValue(repeat);
    }

    //
    private int getNextIndex() { // no repeat
        if(shuffle) {

        }
        else {
            currentIndex++;
        }

        return -1;
    }

    //
    private void startUpdateCurrentPosition() {
        handler.removeCallbacks(updateCurrentPosition);
        handler.postDelayed(updateCurrentPosition, 100);
    }

    private void stopUpdateCurrentPosition() {
        handler.removeCallbacks(updateCurrentPosition);
    }

}
