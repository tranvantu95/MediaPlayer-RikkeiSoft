package com.rikkeisoft.musicplayer.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio.*;
import android.support.annotation.NonNull;
import android.util.Log;

import com.rikkeisoft.musicplayer.model.base.BaseItem;
import com.rikkeisoft.musicplayer.model.item.AlbumItem;
import com.rikkeisoft.musicplayer.model.item.ArtistItem;
import com.rikkeisoft.musicplayer.model.item.SongItem;

import java.util.ArrayList;
import java.util.List;

public class Loader {

    // initialize
    private static Loader instance;

    public static Loader getInstance() {
        return instance;
    }

    public static void initialize(Context context) {
        if(instance == null) instance = new Loader(context.getContentResolver());
    }

    private ContentResolver contentResolver;

    private Loader(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    //
    private List<SongItem> songs;

    private List<AlbumItem> albums;

    private List<ArtistItem> artists;

    //
    public void clearCache() {
        songs = null;
        albums = null;
        artists = null;
    }

    //
    public List<SongItem> getSongs() {
        return songs != null ? songs : loadSongs();
    }

    public List<AlbumItem> getAlbums() {
        return albums != null ? albums : loadAlbums();
    }

    public List<ArtistItem> getArtists() {
        return artists != null ? artists : loadArtists();
    }

    //
    public SongItem findSong(int songId) {
        return findItem(getSongs(), songId);
    }

    public AlbumItem findAlbum(int albumId) {
        return findItem(getAlbums(), albumId);
    }

    public ArtistItem findArtist(int artistId) {
        return findItem(getArtists(), artistId);
    }

    //
    @NonNull
    public List<SongItem> findSongsOfAlbum(AlbumItem albumItem) {
        return findSongs(albumItem, new FindItems<SongItem, AlbumItem>() {
            @Override
            public int getId(SongItem song) {
                return song.getAlbumId();
            }

            @Override
            public void linked(SongItem songItem, AlbumItem albumItem) {
                songItem.setAlbum(albumItem);
            }
        });
    }

    @NonNull
    public List<SongItem> findSongsOfArtist(ArtistItem artistItem) {
        return findSongs(artistItem, new FindItems<SongItem, ArtistItem>() {
            @Override
            public int getId(SongItem song) {
                return song.getArtistId();
            }

            @Override
            public void linked(SongItem songItem, ArtistItem artistItem) {
                songItem.setArtist(artistItem);
            }
        });
    }

    @NonNull
    public List<AlbumItem> findAlbums(ArtistItem artistItem) {
        return findItems(getAlbums(), artistItem, new FindItems<AlbumItem, ArtistItem>() {
            @Override
            public int getId(AlbumItem albumItem) {
                return albumItem.getArtistId();
            }

            @Override
            public void linked(AlbumItem albumItem, ArtistItem artistItem) {
                albumItem.setArtist(artistItem);
            }
        });
    }

    //
    @NonNull
    private <Item2 extends BaseItem> List<SongItem> findSongs(Item2 item2, FindItems<SongItem, Item2> findItems) {
        return findItems(getSongs(), item2, findItems);
    }

    //
    private static <Item extends BaseItem> Item findItem(List<Item> items, int id) {
        for(int i = items.size() - 1; i >= 0; i--) {
            Item item = items.get(i);
            if(id == item.getId()) return item;
        }

        return null;
    }

    @NonNull
    private static <Item extends BaseItem, Item2 extends BaseItem> List<Item> findItems(
            List<Item> items, Item2 item2, FindItems<Item, Item2> findItems) {
        List<Item> _items = new ArrayList<>();

        for(int i = items.size() - 1; i >= 0; i--) {
            Item item = items.get(i);
            if(item2.getId() == findItems.getId(item)) {
                findItems.linked(item, item2);
                _items.add(0, item);
            }
        }

        return _items;
    }

    private interface FindItems<Item, Item2> {
        int getId(Item item);
        void linked(Item item, Item2 item2);
    }

    //
    @NonNull
    private List<SongItem> loadSongs() {
        Log.d("debug", "loadSongs");

        songs = new ArrayList<>();

        Uri uri = Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                Media._ID,     // song id
                Media.TITLE,   // song name
                Media.ALBUM_ID,
                Media.ALBUM,
                Media.ARTIST_ID,
                Media.ARTIST,
                Media.DURATION,
                Media.DATA,
//                Media.DISPLAY_NAME,
        };

        String selection = Media.IS_MUSIC + " != 0";

        String sortOder = Media.TITLE + " ASC";

//        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(
                uri,
                projection,
                selection,
                null,
                sortOder);

        if(cursor != null) {
            while(cursor.moveToNext()) {
                SongItem song = new SongItem();

                int i = 0;

                song.setId(cursor.getInt(i));
                song.setName(cursor.getString(++i));
                song.setAlbumId(cursor.getInt(++i));
                song.setAlbumName(cursor.getString(++i));
                song.setArtistId(cursor.getInt(++i)); //Log.d("debug", song.getArtistId());
                song.setArtistName(cursor.getString(++i));
                song.setDuration(cursor.getInt(++i));
                song.setPath(cursor.getString(++i));

                songs.add(song);
            }

            cursor.close();
        }

        return songs;
    }

    @NonNull
    private List<AlbumItem> loadAlbums() {
        Log.d("debug", "loadAlbums");

        albums = new ArrayList<>();

        Uri uri = Albums.EXTERNAL_CONTENT_URI;

        String[] projection = {
                Albums._ID,
                Albums.ALBUM,
                Media.ARTIST_ID,
                Albums.ARTIST,
                Artists.Albums.ALBUM_ART
        };

        String sortOder = Albums.ALBUM + " ASC";

//        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(
                uri,
                projection,
                null,
                null,
                sortOder);

        if(cursor != null) {
            while(cursor.moveToNext()) {
                AlbumItem album = new AlbumItem();

                int i = 0;

                album.setId(cursor.getInt(i));
                album.setName(cursor.getString(++i));
                album.setArtistId(cursor.getInt(++i));
                album.setArtistName(cursor.getString(++i));
                album.setAlbumArt(cursor.getString(++i)); //Log.d("debug", album.getAlbumArt());

                albums.add(album);
            }

            cursor.close();
        }

        return albums;
    }

    @NonNull
    private List<ArtistItem> loadArtists() {
        Log.d("debug", "loadArtists");

        artists = new ArrayList<>();

        Uri uri = Artists.EXTERNAL_CONTENT_URI;

        String[] projection = {
                Artists._ID,
                Artists.ARTIST,
                Artists.NUMBER_OF_ALBUMS,
                Artists.NUMBER_OF_TRACKS
        };

        String sortOder = Artists.ARTIST + " ASC";

//        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(
                uri,
                projection,
                null,
                null,
                sortOder);

        if(cursor != null) {
            while(cursor.moveToNext()) {
                ArtistItem artist = new ArtistItem();

                int i = 0;

                artist.setId(cursor.getInt(i));
                artist.setName(cursor.getString(++i));
                artist.setNumberOfAlbums(cursor.getInt(++i));
                artist.setNumberOfSongs(cursor.getInt(++i));

                artists.add(artist);
            }

            cursor.close();
        }

        return artists;
    }

}
