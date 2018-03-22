package com.rikkeisoft.musicplayer.model.item;

import android.content.Context;

import com.rikkeisoft.musicplayer.model.base.BaseItem;
import com.rikkeisoft.musicplayer.utils.Loader;

import java.util.List;

public class ArtistItem extends BaseItem {

    private List<AlbumItem> albums;
    private List<SongItem> songs;

    public List<AlbumItem> getAlbums(Context context) {
        if(albums == null) setAlbums(Loader.findAlbums(context, getId()));

        return albums;
    }

    public void setAlbums(List<AlbumItem> albums) {
        this.albums = albums;
    }

    public List<SongItem> getSongs(Context context) {
        if(songs == null) setSongs(Loader.findSongsOfArtist(context, getId()));

        return songs;
    }

    public void setSongs(List<SongItem> songs) {
        this.songs = songs;
    }
}
