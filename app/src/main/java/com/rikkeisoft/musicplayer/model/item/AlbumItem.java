package com.rikkeisoft.musicplayer.model.item;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.rikkeisoft.musicplayer.model.base.BaseItem;
import com.rikkeisoft.musicplayer.utils.Loader;

import java.util.List;

public class AlbumItem extends BaseItem {

    // media info
    private String albumArt;
    private int artistId;
    private String artistName;

    // linked
    private ArtistItem artist;
    private List<SongItem> songs;

    // method
    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    @Override
    public Bitmap getBitmap() {
        if(bitmap == null && getAlbumArt() != null)
            setBitmap(BitmapFactory.decodeFile(getAlbumArt()));

        return bitmap;
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
