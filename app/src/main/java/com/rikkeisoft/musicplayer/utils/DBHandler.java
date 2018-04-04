package com.rikkeisoft.musicplayer.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.rikkeisoft.musicplayer.db.AppDatabase;
import com.rikkeisoft.musicplayer.db.MySQLite;
import com.rikkeisoft.musicplayer.db.Song;
import com.rikkeisoft.musicplayer.model.item.SongItem;

import java.util.ArrayList;
import java.util.List;

public class DBHandler {

    public static class PlaylistLoader extends AsyncTask<String, Void, List<SongItem>> {

        private MySQLite mySQLite;

        private Callback callback;

        public PlaylistLoader(Context context, Callback callback) {
            mySQLite = MySQLite.getInstance(context);
            this.callback = callback;
        }

        @Override
        protected List<SongItem> doInBackground(String... args) {
            List<SongItem> playlist = new ArrayList<>();
            List<SongItem> songs = mySQLite.getPlaylist(args[0]);
            List<SongItem> songItems = Loader.getInstance().getSongs();
//            Collections.sort(songItems, new Comparator<SongItem>() {
//                @Override
//                public int compare(SongItem songItem, SongItem t1) {
//                    return songItem.getId() - t1.getId();
//                }
//            });
            int size = songItems.size();
            for(int i = 0; i < size; i++) {
                SongItem songItem = songItems.get(i);
                int size2 = songs.size();
                if(size2 == 0) break;
                for(int j = 0; j < size2; j++) {
                    if (songs.get(j).getId() == songItem.getId()) {
                        playlist.add(songItem);
                        songs.remove(j);
                        break;
                    }
                }
            }

            return playlist;
        }

        @Override
        protected void onPostExecute(List<SongItem> songItems) {
            callback.onResult(songItems);
        }

        public interface Callback {
            void onResult(List<SongItem> playlist);
        }
    }

    public static class PlaylistSaver extends AsyncTask<String, Void, Void> {

        private MySQLite mySQLite;

        private List<SongItem> songs;

        public PlaylistSaver(Context context, List<SongItem> songs) {
            mySQLite = MySQLite.getInstance(context);
            this.songs = songs;
        }

        @Override
        protected Void doInBackground(String... args) {
            mySQLite.deletePlaylist(args[0]);
            mySQLite.addPlaylist(args[0], songs);

            return null;
        }

    }
}
