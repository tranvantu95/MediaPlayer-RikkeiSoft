package com.ccs.app.musicplayer.model.item;

import android.content.Context;
import android.graphics.Bitmap;

import com.ccs.app.musicplayer.model.base.BaseItem;
import com.ccs.app.musicplayer.utils.Loader;

import java.util.List;

public class ArtistItem extends BaseItem {

    // media tvInfo
    private int numberOfAlbums;
    private int numberOfSongs;

    // linked
    private List<AlbumItem> albums;
    private List<SongItem> songs;

    // method
    public int getNumberOfAlbums() {
        return numberOfAlbums;
    }

    public void setNumberOfAlbums(int numberOfAlbums) {
        this.numberOfAlbums = numberOfAlbums;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    public void setNumberOfSongs(int numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }

    @Override
    public String getInfo() {
        int na = getNumberOfAlbums();
        int ns = getNumberOfSongs();
        return "" + na + " album" + (na > 1 ? "s" : "") + " | " + ns + " song" + (ns > 1 ? "s" : "");
    }

    @Override
    public Bitmap getBitmap() {
        if(bitmap == null && !getAlbums().isEmpty()) {
            for(int i = getAlbums().size() - 1; i >= 0; i--)
                if(getAlbums().get(i).getBitmap() != null) {
                    setBitmap(getAlbums().get(i).getBitmap());
                    break;
                }
        }

        return bitmap;
    }

    public List<AlbumItem> getAlbums() {
        if(albums == null) setAlbums(Loader.getInstance().findAlbums(this));

        return albums;
    }

    public void setAlbums(List<AlbumItem> albums) {
        this.albums = albums;
    }

    public List<SongItem> getSongs() {
        if(songs == null) setSongs(Loader.getInstance().findSongsOfArtist(this));

        return songs;
    }

    public void setSongs(List<SongItem> songs) {
        this.songs = songs;
    }
}
