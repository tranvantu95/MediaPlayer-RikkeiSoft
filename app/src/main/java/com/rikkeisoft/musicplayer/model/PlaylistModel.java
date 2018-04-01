package com.rikkeisoft.musicplayer.model;

import android.arch.lifecycle.MutableLiveData;

public class PlaylistModel extends SongsModel {

    private MutableLiveData<Integer> top;

    public MutableLiveData<Integer> getTop() {
        if(top == null) top = new MutableLiveData<>();

        return top;
    }

}
