package com.rikkeisoft.musicplayer.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio.*;

import com.rikkeisoft.musicplayer.model.MusicInfo;
import com.rikkeisoft.musicplayer.model.item.AlbumItem;
import com.rikkeisoft.musicplayer.model.item.SongItem;

import java.util.ArrayList;
import java.util.List;

public class Loader {

    public static List<SongItem> loadSongs(Context context) {
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
        Uri uri = Albums.EXTERNAL_CONTENT_URI;

        String[] projection = {
                Albums._ID,
                Albums.ALBUM_ART
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

            while(cursor.moveToNext()) {
                AlbumItem album = new AlbumItem();

                album.setId(cursor.getString(0));
                album.setName(cursor.getString(1));

                albums.add(album);
            }

            cursor.close();
        }

        return albums;
    }

    public static List<MusicInfo> loadMusicInfo(Context context) {
        String selection = Media.IS_MUSIC + " != 0";

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

        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(
                Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        List<MusicInfo> musicInfoList = null;

        if(cursor != null) {
            musicInfoList = new ArrayList<>();

            while(cursor.moveToNext()) {
                MusicInfo musicInfo = new MusicInfo();

                musicInfo.setPath(cursor.getString(0));
                musicInfo.setSongId(cursor.getString(1));
                musicInfo.setSongName(cursor.getString(2));
                musicInfo.setAlbumId(cursor.getString(3));
                musicInfo.setAlbumName(cursor.getString(4));
                musicInfo.setArtistId(cursor.getString(5));
                musicInfo.setArtistName(cursor.getString(6));
                musicInfo.setDuration(cursor.getString(7)); android.util.Log.d("debug", musicInfo.getAlbumId());

                musicInfoList.add(musicInfo);
            }

            cursor.close();
        }

        return musicInfoList;
    }
}
