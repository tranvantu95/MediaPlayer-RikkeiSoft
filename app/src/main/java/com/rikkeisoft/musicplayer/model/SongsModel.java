package com.rikkeisoft.musicplayer.model;

import android.arch.lifecycle.MutableLiveData;

import com.rikkeisoft.musicplayer.model.base.MyModel;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.player.PlaylistPlayer;

public class SongsModel extends MyModel<SongItem> {

    private MutableLiveData<PlaylistPlayer> playlistPlayer;

    public MutableLiveData<PlaylistPlayer> getPlaylistPlayer() {
        if(playlistPlayer == null) playlistPlayer = new MutableLiveData<>();

        return playlistPlayer;
    }

}
