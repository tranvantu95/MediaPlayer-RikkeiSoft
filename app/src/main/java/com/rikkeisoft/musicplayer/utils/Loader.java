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

    private static List<SongItem> songs;

    private static List<AlbumItem> albums;

    private static List<ArtistItem> artists;

    //
    public static void clearCache() {
        songs = null;
        albums = null;
        artists = null;
    }

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
    public static List<SongItem> findSongsOfAlbum(Context context, AlbumItem albumItem) {
        return findSongs(context, albumItem, new FindItems<SongItem, AlbumItem>() {
            @Override
            public String getId(SongItem song) {
                return song.getAlbumId();
            }

            @Override
            public void linked(SongItem songItem, AlbumItem albumItem) {
                songItem.setAlbum(albumItem);
            }
        });
    }

    @NonNull
    public static List<SongItem> findSongsOfArtist(Context context, ArtistItem artistItem) {
        return findSongs(context, artistItem, new FindItems<SongItem, ArtistItem>() {
            @Override
            public String getId(SongItem song) {
                return song.getArtistId();
            }

            @Override
            public void linked(SongItem songItem, ArtistItem artistItem) {
                songItem.setArtist(artistItem);
            }
        });
    }

    @NonNull
    public static List<AlbumItem> findAlbums(Context context, ArtistItem artistItem) {
        if(albums == null) loadAlbums(context);

        return findItems(albums, artistItem, new FindItems<AlbumItem, ArtistItem>() {
            @Override
            public String getId(AlbumItem albumItem) {
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
    private static <Item2 extends BaseItem> List<SongItem> findSongs(
            Context context, Item2 item2, FindItems<SongItem, Item2> findItems) {
        if(songs == null) loadSongs(context);

        return findItems(songs, item2, findItems);
    }

    //
    private static <Item extends BaseItem> Item findItem(List<Item> items, String id) {
        for(int i = items.size() - 1; i >= 0; i--) {
            Item item = items.get(i);
            if(id.equals(item.getId())) return item;
        }

        return null;
    }

    @NonNull
    private static <Item extends BaseItem, Item2 extends BaseItem> List<Item> findItems(
            List<Item> items, Item2 item2, FindItems<Item, Item2> findItems) {
        List<Item> _items = new ArrayList<>();

        for(int i = items.size() - 1; i >= 0; i--) {
            Item item = items.get(i);
            if(item2.getId().equals(findItems.getId(item))) {
                findItems.linked(item, item2);
                _items.add(item);
            }
        }

        return _items;
    }

    public interface FindItems<Item, Item2> {
        String getId(Item item);
        void linked(Item item, Item2 item2);
    }

    //
    public static List<SongItem> loadSongs(Context context) {
        if(Loader.songs != null) return Loader.songs;

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

            Log.d("debug", "song----------------------");

            while(cursor.moveToNext()) {
                SongItem song = new SongItem();

                int i = 0;

                song.setId(cursor.getString(i));
                song.setName(cursor.getString(++i));
                song.setAlbumId(cursor.getString(++i));
                song.setAlbumName(cursor.getString(++i));
                song.setArtistId(cursor.getString(++i)); //Log.d("debug", song.getArtistId());
                song.setArtistName(cursor.getString(++i));
                song.setDuration(cursor.getString(++i));
                song.setPath(cursor.getString(++i));

                songs.add(song);
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
                Media.ARTIST_ID,
                Albums.ARTIST,
                Artists.Albums.ALBUM_ART
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

            Log.d("debug", "album----------------------");

            while(cursor.moveToNext()) {
                AlbumItem album = new AlbumItem();

                int i = 0;

                album.setId(cursor.getString(i));
                album.setName(cursor.getString(++i));
                album.setArtistId(cursor.getString(++i));
                album.setArtistName(cursor.getString(++i));
                album.setAlbumArt(cursor.getString(++i)); //Log.d("debug", album.getAlbumArt());

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
                Artists.ARTIST,
                Artists.NUMBER_OF_ALBUMS
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

                int i = 0;

                artist.setId(cursor.getString(i));
                artist.setName(cursor.getString(++i));
                artist.setNumberOfAlbums(cursor.getInt(++i));

                artists.add(artist);
            }

            cursor.close();
        }

        return artists;
    }

}
