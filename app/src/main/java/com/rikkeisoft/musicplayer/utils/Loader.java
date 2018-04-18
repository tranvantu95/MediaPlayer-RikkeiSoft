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

    private boolean isLoaded;

    public boolean isLoaded() {
        return isLoaded;
    }

    private long latestTimeClearCache;

    //
    public void clearCache() {
        if(System.currentTimeMillis() - latestTimeClearCache < 1000) return;
        Log.d("debug", "clearCache " + getClass().getSimpleName());
        latestTimeClearCache = System.currentTimeMillis();

        songs = null;
        albums = null;
        artists = null;
        isLoaded = false;
    }

    //
    public synchronized void loadAll() {
        if(isLoaded) return;
        Log.d("debug", "loadAll " + getClass().getSimpleName());
        getSongs();
        getAlbums();
        getArtists();
        isLoaded = songs != null;
    }

    //
    public synchronized List<SongItem> getSongs() {
        return songs != null ? songs : loadSongs();
    }

    public synchronized List<AlbumItem> getAlbums() {
        return albums != null ? albums : loadAlbums();
    }

    public synchronized List<ArtistItem> getArtists() {
        return artists != null ? artists : loadArtists();
    }

    //
    public SongItem findSong(int songId) {
        return ArrayUtils.findItem(getSongs(), songId);
    }

    public AlbumItem findAlbum(int albumId) {
        return ArrayUtils.findItem(getAlbums(), albumId);
    }

    public ArtistItem findArtist(int artistId) {
        return ArrayUtils.findItem(getArtists(), artistId);
    }

    //
    @NonNull
    public List<SongItem> findSongsOfAlbum(AlbumItem albumItem) {
        return ArrayUtils.findItems(getSongs(), albumItem, new ArrayUtils.FindItems<SongItem, AlbumItem>() {
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
        return ArrayUtils.findItems(getSongs(), artistItem, new ArrayUtils.FindItems<SongItem, ArtistItem>() {
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
    public List<AlbumItem> findAlbums(ArtistItem artist) {
        List<AlbumItem> result = new ArrayList<>();

        List<AlbumItem> albums = getAlbums();

        for(int i = albums.size() - 1; i >= 0; i--) {
            AlbumItem album = albums.get(i);

            if(album.getArtistId() == artist.getId()) {
                result.add(0, album);
                album.setArtist(artist);
                continue;
            }

            List<SongItem> songs = album.getSongs();

            for(int j = songs.size() - 1; j >= 0; j--) {
                SongItem song = songs.get(j);

                if(song.getArtistId() == artist.getId()) {
                    result.add(0, album);
                    album.setArtist(artist);
                    break;
                }
            }
        }

        return result;
    }

    //
    @NonNull
    private List<SongItem> loadSongs() {
        if(!General.isPermissionGranted) return new ArrayList<>();

        Log.d("debug", "---loadSongs");

        songs = new ArrayList<>();

        Uri uri = Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                Media._ID,     // song id
                Media.TITLE,   // song name
//                Media.DISPLAY_NAME,
                Media.ALBUM_ID,
                Media.ALBUM,
                Media.ARTIST_ID,
                Media.ARTIST,
                Media.DURATION,
                Media.DATA,
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
                song.setArtistId(cursor.getInt(++i));
                song.setArtistName(cursor.getString(++i));
                song.setDuration(cursor.getInt(++i));
                song.setPath(cursor.getString(++i)); //Log.d("debug", song.getPath());

                songs.add(song);
            }

            cursor.close();
        }

        return songs;
    }

    @NonNull
    private List<AlbumItem> loadAlbums() {
        if(!General.isPermissionGranted) return new ArrayList<>();

        Log.d("debug", "---loadAlbums");

        albums = new ArrayList<>();

        Uri uri = Albums.EXTERNAL_CONTENT_URI;

        String[] projection = {
                Albums._ID,
                Albums.ALBUM,
                Albums.ALBUM_ART,
                Media.ARTIST_ID,
                Albums.ARTIST,
                Albums.NUMBER_OF_SONGS,
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
                album.setAlbumArt(cursor.getString(++i)); //Log.d("debug", album.getAlbumArt());
                album.setArtistId(cursor.getInt(++i));
                album.setArtistName(cursor.getString(++i));
                album.setNumberOfSongs(cursor.getInt(++i));

                albums.add(album);
            }

            cursor.close();
        }

        return albums;
    }

    @NonNull
    private List<ArtistItem> loadArtists() {
        if(!General.isPermissionGranted) return new ArrayList<>();

        Log.d("debug", "---loadArtists");

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
