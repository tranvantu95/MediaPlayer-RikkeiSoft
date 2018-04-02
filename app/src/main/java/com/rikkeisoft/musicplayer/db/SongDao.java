package com.rikkeisoft.musicplayer.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface SongDao {

    @Query("SELECT * FROM song")
    List<Song> getAll();

    @Insert
    void insertAll(Song... songs);

    @Insert
    void insert(Song song);

    @Delete
    void delete(Song song);

    @Query("DELETE FROM song")
    void deleteAll();

}
