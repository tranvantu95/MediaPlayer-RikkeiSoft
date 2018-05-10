package com.ccs.app.musicplayer.player;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ccs.app.musicplayer.db.MySQLite;
import com.ccs.app.musicplayer.model.base.BaseItem;
import com.ccs.app.musicplayer.model.item.SongItem;
import com.ccs.app.musicplayer.utils.ArrayUtils;
import com.ccs.app.musicplayer.utils.Loader;

import java.util.ArrayList;
import java.util.List;

public class PlaylistHandler {

    public static class PlaylistLoader extends AsyncTask<String, Void, List<SongItem>> {

        private MySQLite mySQLite;

        private Callback callback;

        private List<SongItem> oldPlaylist;

        public PlaylistLoader(Context context, List<SongItem> oldPlaylist, Callback callback) {
            mySQLite = MySQLite.getInstance(context);
            this.callback = callback;
            if(oldPlaylist != null) this.oldPlaylist = new ArrayList<>(oldPlaylist);
        }

        @Override
        protected List<SongItem> doInBackground(String... args) {
            Loader.getInstance().loadAll();
            List<SongItem> rootList = Loader.getInstance().getSongs();

            if(oldPlaylist == null) {
                Log.d("debug", "loadPlaylistDatabase " + getClass().getSimpleName());
                oldPlaylist = mySQLite.getPlaylist(args[0]);
            }

            return ArrayUtils.searchList(rootList, oldPlaylist);
        }

        @Override
        protected void onPostExecute(List<SongItem> newPlaylist) {
            callback.onResult(newPlaylist);
        }

        public interface Callback {
            void onResult(List<SongItem> playlist);
        }
    }

    public static class PlaylistSaver extends AsyncTask<String, Void, Void> {

        private MySQLite mySQLite;

        private List<SongItem> playlist;

        public PlaylistSaver(Context context, List<SongItem> playlist) {
            mySQLite = MySQLite.getInstance(context);
            this.playlist = new ArrayList<>(playlist);
        }

        @Override
        protected Void doInBackground(String... args) {
            Log.d("debug", "savePlaylistDatabase " + getClass().getSimpleName());
            mySQLite.deletePlaylist(args[0]);
            mySQLite.addPlaylist(args[0], playlist);

            return null;
        }

    }
}
