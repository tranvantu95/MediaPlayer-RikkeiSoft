package com.rikkeisoft.musicplayer.model.item;

import android.content.Context;

import com.rikkeisoft.musicplayer.model.base.BaseItem;
import com.rikkeisoft.musicplayer.utils.Loader;

import java.util.List;

public class SongItem extends BaseItem {

    private String path;
    private String albumId;
    private String albumName;
    private String artistId;
    private String artistName;
    private int duration;

    private AlbumItem album;
    private ArtistItem artist;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = Integer.parseInt(duration);
    }

    public AlbumItem getAlbum(Context context) {
        if(album == null) setAlbum(Loader.findAlbum(context, getAlbumId()));

        return album;
    }

    public void setAlbum(AlbumItem album) {
        this.album = album;
    }

    public ArtistItem getArtist(Context context) {
        if(artist == null) setArtist(Loader.findArtist(context, getArtistId()));

        return artist;
    }

    public void setArtist(ArtistItem artist) {
        this.artist = artist;
    }
}
