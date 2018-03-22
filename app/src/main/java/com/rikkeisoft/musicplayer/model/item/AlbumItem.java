package com.rikkeisoft.musicplayer.model.item;

import android.content.Context;

import com.rikkeisoft.musicplayer.model.base.BaseItem;
import com.rikkeisoft.musicplayer.utils.Loader;

import java.util.List;

public class AlbumItem extends BaseItem {

    private String artistId;
    private String artistName;

    private ArtistItem artist;
    private List<SongItem> songs;

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public ArtistItem getArtist(Context context) {
        if(artist == null) setArtist(Loader.findArtist(context, getArtistId()));

        return artist;
    }

    public void setArtist(ArtistItem artist) {
        this.artist = artist;
    }

    public List<SongItem> getSongs(Context context) {
        if(songs == null) setSongs(Loader.findSongsOfAlbum(context, getId()));

        return songs;
    }

    public void setSongs(List<SongItem> songs) {
        this.songs = songs;
    }
}
