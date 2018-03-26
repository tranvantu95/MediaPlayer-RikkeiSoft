package com.rikkeisoft.musicplayer.model.item;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.rikkeisoft.musicplayer.model.base.BaseItem;
import com.rikkeisoft.musicplayer.utils.Loader;

import java.util.List;

public class AlbumItem extends BaseItem {

    private String albumArt;
    private Bitmap albumArtBitmap;

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

    public Bitmap getAlbumArtBitmap() {
        if(albumArtBitmap == null && getAlbumArt() != null)
            setAlbumArtBitmap(BitmapFactory.decodeFile(getAlbumArt()));
        return albumArtBitmap;
    }

    public void setAlbumArtBitmap(Bitmap albumArtBitmap) {
        this.albumArtBitmap = albumArtBitmap;
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
