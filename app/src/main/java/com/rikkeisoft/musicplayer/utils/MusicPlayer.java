package com.rikkeisoft.musicplayer.utils;

import android.media.AudioManager;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.rikkeisoft.musicplayer.model.item.SongItem;

import java.io.IOException;
import java.util.List;

public class MusicPlayer implements MusicPlayerInterface, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    public static final int UN_REPEAT = 0;
    public static final int REPEAT_PLAYLIST = 1;
    public static final int REPEAT_SONG = 2;

    private List<SongItem> playlist;

    public List<SongItem> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(List<SongItem> playlist) {
        this.playlist = playlist;
    }

    private Callback callback;

    private MediaPlayer mediaPlayer;

    private int repeat;
    private boolean shuffle;

    private int currentIndex;
    private boolean paused = true;

//    private static MusicPlayer instance;
//
//    public static MusicPlayer getInstance() {
//        if(instance == null) instance = new MusicPlayer();
//        return instance;
//    }

    public MusicPlayer(Callback callback) {
        this.callback = callback;

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                Log.d("debug", "onBufferingUpdate " + i);

            }
        });
    }

    public void prepare(String path) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
//            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.prepareAsync();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void prepare(SongItem song) {
        prepare(song.getPath());
    }

    @Override
    public void prepare(int index) {
        prepare(playlist.get(index));
    }

    @Override
    public void play(int index) {
        currentIndex = index;
        prepare(index);
    }

    @Override
    public void play() {
        paused = false;
        mediaPlayer.start();
        runUpdateTime();
        callback.onPlay(this);
    }

    @Override
    public void pause() {
        paused = true;
        mediaPlayer.pause();
        removeUpdateTime();
        callback.onPause(this);
    }

    @Override
    public void replay() {
        mediaPlayer.seekTo(0);
        play();
    }

    @Override
    public void previous() {

    }

    @Override
    public void next() {
        if(repeat == REPEAT_SONG) {
//            replay();
            return;
        }

        if(getNextIndex() < 0) {
            return;
        }

        play(currentIndex);
    }

    @Override
    public void setShuffle(boolean shuffle) {

    }

    @Override
    public void setRepeat(int repeat) {
        this.repeat = repeat;
        mediaPlayer.setLooping(repeat == REPEAT_SONG);
        callback.onToggleRepeat(this);
    }

    @Override
    public void seekTo(int mc) {
        mediaPlayer.seekTo(mc);
    }

    public void togglePause() {
        paused = !paused;
        if(paused) pause();
        else play();
    }

    public void toggleShuffle() {
        shuffle = !shuffle;
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    public int getRepeat() {
        return repeat;
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentTime() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void onUpdateTime(int ms) {

    }

    @Override
    public void onPlay(int index) {

    }

    @Override
    public void onPause(int index) {

    }

    @Override
    public void onCompleteSong(int index) {

    }

    @Override
    public void onCompletePlaylist(int index) {

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("debug", "onPrepared " + getClass().getSimpleName());
        play();
        callback.onPrepared(this);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d("debug", "onCompletion " + getClass().getSimpleName());
        next();
    }

    private int getNextIndex() {
        if(shuffle) {

        }
        else {
            currentIndex++;
            if(currentIndex >= playlist.size()) {
                if (repeat == REPEAT_PLAYLIST) currentIndex = 0;
                else return -1;
            }
        }

        return currentIndex;
    }

    ////
    private Handler handler;

    private Runnable runnable;

    public void runUpdateTime() {
        if(handler == null) handler = new Handler();
        if(runnable == null) runnable = new Runnable() {
            @Override
            public void run() {
                callback.onUpdateTime(MusicPlayer.this);

                handler.postDelayed(this, 100);
            }
        };

        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 100);
    }

    public void removeUpdateTime() {
        handler.removeCallbacks(runnable);
    }

    public static String formatTime(int ms) {
        int s = ms / 1000;
        int h = s / 3600;
        int m = s % 3600 / 60;
        s %= 60;

        if(h > 0) return "" + h + ":" + m + ":" + s;
        return "" + m + ":" + s;
    }

    public interface Callback {
        void onPrepared(MusicPlayer musicPlayer);
        void onUpdateTime(MusicPlayer musicPlayer);
        void onPause(MusicPlayer musicPlayer);
        void onPlay(MusicPlayer musicPlayer);
        void onToggleRepeat(MusicPlayer musicPlayer);
    }

}
