package com.rikkeisoft.musicplayer.utils;

public interface MusicPlayerInterface {

    // action
    void prepare(int index);
    void play(int index);
    void play();
    void pause();
    void replay();
    void previous();
    void next();
    void setShuffle(boolean shuffle);
    void setRepeat(int repeat);
    void seekTo(int ms);

    // get
    boolean isPlaying();
    boolean isRunning();
    int getDuration();
    int getCurrentTime();

    //
    void onUpdateTime(int ms);
    void onPlay(int index);
    void onPause(int index);
    void onCompleteSong(int index);
    void onCompletePlaylist(int index);
}
