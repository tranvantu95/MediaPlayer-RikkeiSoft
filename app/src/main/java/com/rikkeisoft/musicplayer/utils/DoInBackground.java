package com.rikkeisoft.musicplayer.utils;

import android.os.AsyncTask;

import com.rikkeisoft.musicplayer.db.AppDatabase;
import com.rikkeisoft.musicplayer.db.Song;

import java.util.List;

public class DoInBackground {

    public static class dbLoader extends AsyncTask<Void, Void, List<Song>> {

        private AppDatabase db;

        public dbLoader(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected List<Song> doInBackground(Void... voids) {
            return db.songDao().getAll();
        }
    }
}
