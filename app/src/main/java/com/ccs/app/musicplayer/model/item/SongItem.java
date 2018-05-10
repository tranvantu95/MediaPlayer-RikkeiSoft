package com.ccs.app.musicplayer.model.item;

import android.graphics.Bitmap;

import com.ccs.app.musicplayer.model.base.BaseItem;
import com.ccs.app.musicplayer.utils.Loader;

public class SongItem extends BaseItem {

    // media tvInfo
    private int albumId;
    private String albumName;
    private int artistId;
    private String artistName;
    private int duration;
    private String path;

    // linked
    private AlbumItem album;
    private ArtistItem artist;

    // method
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String getInfo() {
        return getArtistName() + " | " + getAlbumName();
    }

    @Override
    public Bitmap getBitmap() {
        if(bitmap == null) setBitmap(getAlbum().getBitmap());

        return bitmap;
    }

    public AlbumItem getAlbum() {
        if(album == null) setAlbum(Loader.getInstance().findAlbum(getAlbumId()));

        return album;
    }

    public void setAlbum(AlbumItem album) {
        this.album = album;
    }

    public ArtistItem getArtist() {
        if(artist == null) setArtist(Loader.getInstance().findArtist(getArtistId()));

        return artist;
    }

    public void setArtist(ArtistItem artist) {
        this.artist = artist;
    }
}
