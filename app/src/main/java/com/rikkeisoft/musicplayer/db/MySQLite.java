package com.rikkeisoft.musicplayer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rikkeisoft.musicplayer.model.item.SongItem;

import java.util.ArrayList;
import java.util.List;

public class MySQLite extends SQLiteOpenHelper {

    // DATABASE
    private static final String DATABASE_NAME = "app_database";
    private static final int DATABASE_VERSION = 1;

    // TABLE
    private static final String TABLE_SONG = "song";
    private static final String TABLE_PLAYLIST = "playlist";

    // COLUMN
    private static final String KEY_ID = "id";
    private static final String KEY_SONG_ID = "song_id";
    private static final String KEY_PLAYLIST_NAME = "playlist_name";

    // instance
    private static MySQLite instance = null;
    public static MySQLite getInstance(Context context) {
        if (instance == null) instance = new MySQLite(context.getApplicationContext());
        return instance;
    }

    private MySQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SONG
                + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_SONG_ID + " INTEGER, "
                + KEY_PLAYLIST_NAME + " TEXT"
                + ")"
        );

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PLAYLIST
                + "("
                + KEY_PLAYLIST_NAME + " INTEGER PRIMARY KEY"
                + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public List<SongItem> getPlaylist(String playlistName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + KEY_SONG_ID + " FROM " + TABLE_SONG
                + " WHERE " + KEY_PLAYLIST_NAME + " = ?", new String[]{playlistName});

        List<SongItem> songs = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                SongItem song = new SongItem();
                song.setId(cursor.getInt(0));
                songs.add(song);
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return songs;
    }

    public void addPlaylist(String playlistName, List<SongItem> songs) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLAYLIST_NAME, playlistName);
        db.insert(TABLE_PLAYLIST, "", values);

        int size = songs.size();
        for(int i = 0; i < size; i++) {
//            ContentValues values = new ContentValues();
//            values.put(KEY_PLAYLIST_NAME, playlistName);
            values.put(KEY_SONG_ID, songs.get(i).getId());
            db.insert(TABLE_SONG, "", values);
        }

        db.close();
    }

    public void deletePlaylist(String playlistName) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_SONG, KEY_PLAYLIST_NAME + " = ?", new String[] {playlistName});
        db.delete(TABLE_PLAYLIST, KEY_PLAYLIST_NAME + " = ?", new String[] {playlistName});

        db.close();
    }

}
