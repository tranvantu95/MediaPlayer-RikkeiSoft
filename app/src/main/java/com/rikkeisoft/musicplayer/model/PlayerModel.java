package com.rikkeisoft.musicplayer.model;

import android.arch.lifecycle.MutableLiveData;

import com.rikkeisoft.musicplayer.model.base.BaseListModel;
import com.rikkeisoft.musicplayer.model.item.SongItem;

public class PlayerModel extends BaseListModel<SongItem> {

    private MutableLiveData<Integer> playingPosition;

    public MutableLiveData<Integer> getPlayingPosition() {
        if(playingPosition == null) playingPosition = new MutableLiveData<>();

        return playingPosition;
    }

    private MutableLiveData<Integer> duration;

    public MutableLiveData<Integer> getDuration() {
        if(duration == null) duration = new MutableLiveData<>();

        return duration;
    }

    private MutableLiveData<Integer> currentTime;

    public MutableLiveData<Integer> getCurrentTime() {
        if(currentTime == null) currentTime = new MutableLiveData<>();

        return currentTime;
    }

    private MutableLiveData<Boolean> paused;

    public MutableLiveData<Boolean> getPaused() {
        if(paused == null) paused = new MutableLiveData<>();

        return paused;
    }

    private MutableLiveData<Integer> repeat;

    public MutableLiveData<Integer> getRepeat() {
        if(repeat == null) repeat = new MutableLiveData<>();

        return repeat;
    }



}
