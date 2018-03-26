package com.rikkeisoft.musicplayer.model.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.rikkeisoft.musicplayer.model.base.BaseItem;
import com.rikkeisoft.musicplayer.utils.Loader;

import java.util.List;

public class AlbumItem extends BaseItem {

    private String albumArt;
    private Bitmap bmAlbumArt;

    private String artistId;
    private String artistName;

    private ArtistItem artist;
    private List<SongItem> songs;

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public Bitmap getBmAlbumArt() {
        if(bmAlbumArt == null && getAlbumArt() != null)
            setBmAlbumArt(BitmapFactory.decodeFile(getAlbumArt()));
        return bmAlbumArt;
    }

    public void setBmAlbumArt(Bitmap bmAlbumArt) {
        this.bmAlbumArt = bmAlbumArt;
    }

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

    public ArtistItem getArtist() {
        if(artist == null) setArtist(Loader.getInstance().findArtist(getArtistId()));

        return artist;
    }

    public void setArtist(ArtistItem artist) {
        this.artist = artist;
    }

    public List<SongItem> getSongs() {
        if(songs == null) setSongs(Loader.getInstance().findSongsOfAlbum(this));

        return songs;
    }

    public void setSongs(List<SongItem> songs) {
        this.songs = songs;
    }
}
