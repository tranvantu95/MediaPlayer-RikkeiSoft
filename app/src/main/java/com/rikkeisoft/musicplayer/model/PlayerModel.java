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

}
