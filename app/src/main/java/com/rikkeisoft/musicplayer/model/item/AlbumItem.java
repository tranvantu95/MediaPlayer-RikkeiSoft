package com.rikkeisoft.musicplayer.model.item;

import java.util.List;

public class AlbumItem {

    private String name;

    private List<SongItem> songs;

    private List<ArtistItem> artists;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SongItem> getSongs() {
        return songs;
    }

    public void setSongs(List<SongItem> songs) {
        this.songs = songs;
    }

    public List<ArtistItem> getArtists() {
        return artists;
    }

    public void setArtists(List<ArtistItem> artists) {
        this.artists = artists;
    }
}
