package com.rikkeisoft.musicplayer.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio.*;
import android.support.annotation.NonNull;

import com.rikkeisoft.musicplayer.model.base.BaseItem;
import com.rikkeisoft.musicplayer.model.item.AlbumItem;
import com.rikkeisoft.musicplayer.model.item.ArtistItem;
import com.rikkeisoft.musicplayer.model.item.SongItem;

import java.util.ArrayList;
import java.util.List;

public class Loader {

    private static List<SongItem> songs;

    private static List<AlbumItem> albums;

    private static List<ArtistItem> artists;

    //
    public static SongItem findSong(Context context, String songId) {
        if(songs == null) loadSongs(context);

        return findItem(songs, songId);
    }

    public static AlbumItem findAlbum(Context context, String albumId) {
        if(albums == null) loadAlbums(context);

        return findItem(albums, albumId);
    }

    public static ArtistItem findArtist(Context context, String artistId) {
        if(artists == null) loadArtists(context);

        return findItem(artists, artistId);
    }

    //
    @NonNull
    public static List<SongItem> findSongsOfAlbum(Context context, String albumId) {
        return findSongs(context, albumId, new IdSelector<SongItem>() {
            @Override
            public String getId(SongItem songItem) {
                return songItem.getAlbumId();
            }
        });
    }

    @NonNull
    public static List<SongItem> findSongsOfArtist(Context context, String artistId) {
        return findSongs(context, artistId, new IdSelector<SongItem>() {
            @Override
            public String getId(SongItem songItem) {
                return songItem.getArtistId();
            }
        });
    }

    @NonNull
    public static List<AlbumItem> findAlbums(Context context, String artistId) {
        if(albums == null) loadAlbums(context);

        return findItems(albums, artistId, new IdSelector<AlbumItem>() {
            @Override
            public String getId(AlbumItem albumItem) {
                return albumItem.getArtistId();
            }
        });
    }

    //
    @NonNull
    public static List<SongItem> findSongs(Context context, String id, IdSelector<SongItem> idSelector) {
        if(songs == null) loadSongs(context);

        return findItems(songs, id, idSelector);
    }

    //
    public static <Item extends BaseItem> Item findItem(List<Item> items, String id) {
        for(int i = items.size() - 1; i >= 0; i--) {
            Item item = items.get(i);
            if(id.equals(item.getId())) return item;
        }

        return null;
    }

    @NonNull
    public static <Item extends BaseItem> List<Item> findItems(List<Item> items, String id, IdSelector<Item> idSelector) {
        List<Item> _items = new ArrayList<>();

        for(int i = items.size() - 1; i >= 0; i--) {
            Item item = items.get(i);
            if(id.equals(idSelector.getId(item))) _items.add(item);
        }

        return _items;
    }

    public interface IdSelector<Item> {
        String getId(Item item);
    }

    //
    public static void clearCache() {
        songs = null;
        albums = null;
        artists = null;
    }

    //
    public static List<SongItem> loadSongs(Context context) {
        if(Loader.songs != null) return Loader.songs;

        Uri uri = Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                Media.DATA,
//                Media.DISPLAY_NAME,
                Media._ID,     // song id
                Media.TITLE,   // song name
                Media.ALBUM_ID,
                Media.ALBUM,
                Media.ARTIST_ID,
                Media.ARTIST,
                Media.DURATION
        };

        String selection = Media.IS_MUSIC + " != 0";

        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(
                uri,
                projection,
                selection,
                null,
                null);

        List<SongItem> songs = null;

        if(cursor != null) {
            songs = new ArrayList<>();
            Loader.songs = songs;

            while(cursor.moveToNext()) {
                SongItem songItem = new SongItem();

                songItem.setPath(cursor.getString(0));
                songItem.setId(cursor.getString(1));
                songItem.setName(cursor.getString(2));
                songItem.setAlbumId(cursor.getString(3));
                songItem.setAlbumName(cursor.getString(4));
                songItem.setArtistId(cursor.getString(5));
                songItem.setArtistName(cursor.getString(6));
                songItem.setDuration(cursor.getString(7));

                songs.add(songItem);
            }

            cursor.close();
        }

        return songs;
    }

    public static List<AlbumItem> loadAlbums(Context context) {
        if(Loader.albums != null) return Loader.albums;

        Uri uri = Albums.EXTERNAL_CONTENT_URI;

        String[] projection = {
                Albums._ID,
                Albums.ALBUM,
                Albums.ARTIST
        };

//        String selection = Media.IS_MUSIC + " != 0";

        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(
                uri,
                projection,
                null,
                null,
                null);

        List<AlbumItem> albums = null;

        if(cursor != null) {
            albums = new ArrayList<>();
            Loader.albums = albums;

            while(cursor.moveToNext()) {
                AlbumItem album = new AlbumItem();

                album.setId(cursor.getString(0));
                album.setName(cursor.getString(1));
                album.setArtistName(cursor.getString(2));

                albums.add(album);
            }

            cursor.close();
        }

        return albums;
    }

    public static List<ArtistItem> loadArtists(Context context) {
        if(Loader.artists != null) return Loader.artists;

        Uri uri = Artists.EXTERNAL_CONTENT_URI;

        String[] projection = {
                Artists._ID,
                Artists.ARTIST
        };

//        String selection = Media.IS_MUSIC + " != 0";

        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(
                uri,
                projection,
                null,
                null,
                null);

        List<ArtistItem> artists = null;

        if(cursor != null) {
            artists = new ArrayList<>();
            Loader.artists = artists;

            while(cursor.moveToNext()) {
                ArtistItem artist = new ArtistItem();

                artist.setId(cursor.getString(0));
                artist.setName(cursor.getString(1));

                artists.add(artist);
            }

            cursor.close();
        }

        return artists;
    }

}
