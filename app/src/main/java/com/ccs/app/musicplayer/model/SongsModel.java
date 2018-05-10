package com.ccs.app.musicplayer.model;

import android.arch.lifecycle.MutableLiveData;

import com.ccs.app.musicplayer.model.base.MyModel;
import com.ccs.app.musicplayer.model.item.SongItem;
import com.ccs.app.musicplayer.player.PlaylistPlayer;

public class SongsModel extends MyModel<SongItem> {

    private MutableLiveData<PlaylistPlayer> playlistPlayer;

    public MutableLiveData<PlaylistPlayer> getPlaylistPlayer() {
        if(playlistPlayer == null) playlistPlayer = new MutableLiveData<>();

        return playlistPlayer;
    }

}
