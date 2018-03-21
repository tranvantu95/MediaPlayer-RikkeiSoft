package com.rikkeisoft.musicplayer.model.item;

import java.util.List;

public class SongItem {

    private String name;

    private List<AlbumItem> albums;

    private List<ArtistItem> artist;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AlbumItem> getAlbums() {
        return albums;
    }

    public void setAlbums(List<AlbumItem> albums) {
        this.albums = albums;
    }

    public List<ArtistItem> getArtist() {
        return artist;
    }

    public void setArtist(List<ArtistItem> artist) {
        this.artist = artist;
    }
}
