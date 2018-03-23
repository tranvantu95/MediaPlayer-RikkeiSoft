package com.rikkeisoft.musicplayer.model.item;

import android.content.Context;

import com.rikkeisoft.musicplayer.model.base.BaseItem;
import com.rikkeisoft.musicplayer.utils.Loader;

import java.util.List;

public class ArtistItem extends BaseItem {

    private int numberOfAlbums;

    private List<AlbumItem> albums;
    private List<SongItem> songs;

    public int getNumberOfAlbums() {
        return numberOfAlbums;
    }

    public void setNumberOfAlbums(int numberOfAlbums) {
        this.numberOfAlbums = numberOfAlbums;
    }

    //
    public List<AlbumItem> getAlbums(Context context) {
        if(albums == null) setAlbums(Loader.findAlbums(context, this));

        return albums;
    }

    public void setAlbums(List<AlbumItem> albums) {
        this.albums = albums;
    }

    public List<SongItem> getSongs(Context context) {
        if(songs == null) setSongs(Loader.findSongsOfArtist(context, this));

        return songs;
    }

    public void setSongs(List<SongItem> songs) {
        this.songs = songs;
    }
}
