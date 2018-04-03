package com.rikkeisoft.musicplayer.service;

import android.app.Service;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.rikkeisoft.musicplayer.app.MyApplication;
import com.rikkeisoft.musicplayer.db.MySQLite;
import com.rikkeisoft.musicplayer.db.Song;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.utils.Loader;
import com.rikkeisoft.musicplayer.utils.PlaylistPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayerService extends Service {

    public static final String DATA = "player_data";
    public static final String SHUFFLE_KEY = "shuffle";
    public static final String REPEAT_KEY = "repeat";
    public static final String CURRENT_SONG_ID_KEY = "currentSongId";
    public static final String CURRENT_PLAYLIST_ID_KEY = "currentPlaylistId";

    public static final String CURRENT_PLAYLIST_NAME = "Danh sách phát hiện tại";

    private SharedPreferences preferences;

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

//    private class PlayerHandler extends Handler {
//        public PlayerHandler(Looper looper) {
//            super(looper);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            Bundle bundle = msg.getData();
//        }
//    }

//    private HandlerThread playerThread;
//    private Handler playerHandler;
//    private Handler uiHandler;

    private PlaylistPlayer playlistPlayer;

    public PlaylistPlayer getPlaylistPlayer() {
        return playlistPlayer;
    }

    private PlayerModel playerModel;

    public PlayerModel getPlayerModel() {
        return playerModel;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("debug", "onCreate " + getClass().getSimpleName());

//        playerThread = new HandlerThread("playerThread");
//        playerThread.start();
//
//        playerHandler = new PlayerHandler(playerThread.getLooper());
//        uiHandler = new Handler();

        preferences = getApplicationContext().getSharedPreferences(DATA, Context.MODE_PRIVATE);

        createPlaylistPlayer();

        playlistPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                mediaPlayer.release();
                createPlaylistPlayer();
                return true;
            }
        });
    }

    private void createPlaylistPlayer() {
        String playlistId;
        List<SongItem> playlist;
        SongItem currentSong = null;
        int currentIndex = -1;
        boolean shuffle = false;
        int repeat = PlaylistPlayer.UN_REPEAT;

        if(MyApplication.getPlayerModel() == null) {
            playerModel = new PlayerModel();
            MyApplication.setPlayerModel(playerModel);

            playlistId = preferences.getString(CURRENT_PLAYLIST_ID_KEY, null);

//            playlist = Loader.getInstance().getSongs(); // test
            playlist = new ArrayList<>();
            List<Song> songs = MySQLite.getInstance(getApplicationContext()).getPlaylist(CURRENT_PLAYLIST_NAME);
            List<SongItem> songItems = new ArrayList<>(Loader.getInstance().getSongs());
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

            int currentSongId = preferences.getInt(CURRENT_SONG_ID_KEY, -1);
            if(currentSongId != -1) currentIndex = Loader.findIndex(playlist, currentSongId);
            if(currentIndex != -1) currentSong = playlist.get(currentIndex);

            shuffle = preferences.getBoolean(SHUFFLE_KEY, false);
            repeat = preferences.getInt(REPEAT_KEY, PlaylistPlayer.UN_REPEAT);

            playerModel.getTitle().setValue(playlistId);
            playerModel.getItems().setValue(playlist);
            playerModel.getCurrentSong().setValue(currentSong);
            playerModel.getCurrentIndex().setValue(currentIndex);
            playerModel.getShuffle().setValue(shuffle);
            playerModel.getRepeat().setValue(repeat);

            if(currentSong != null) playerModel.getDuration().setValue(currentSong.getDuration());
            playerModel.getCurrentPosition().setValue(0);
        }
        else {
            playerModel = MyApplication.getPlayerModel();

            playlistId = playerModel.getTitle().getValue();
            playlist = playerModel.getItems().getValue();
            currentSong = playerModel.getCurrentSong().getValue();

            if(playerModel.getCurrentIndex().getValue() != null)
                currentIndex = playerModel.getCurrentIndex().getValue();

            if(playerModel.getShuffle().getValue() != null)
                shuffle = playerModel.getShuffle().getValue();

            if(playerModel.getRepeat().getValue() != null)
                repeat = playerModel.getRepeat().getValue();
        }

        playerModel.getPlaying().setValue(false);

        playlistPlayer = new PlaylistPlayer();
        playlistPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        playlistPlayer.initialize(playlistId, playlist, currentSong, currentIndex, shuffle, repeat);
        playlistPlayer.setCallback(new PlaylistPlayer.Callback() {
            @Override
            public void onPlayingChange(PlaylistPlayer playlistPlayer, boolean playing) {
                Log.d("debug", "---onPlayingChange " + playing);
                playerModel.getPlaying().setValue(playing);
            }

            @Override
            public void onReadyChange(PlaylistPlayer playlistPlayer, boolean ready) {
                Log.d("debug", "---onReadyChange " + ready);
                if(ready) {
                    playerModel.getDuration().setValue(playlistPlayer.getDuration());

                    if(playerModel.getCurrentPosition().getValue() != null
                            && playerModel.getCurrentPosition().getValue() > 0) {
                        playlistPlayer.seekTo(playerModel.getCurrentPosition().getValue());
                    }
                }
            }

            @Override
            public void onUpdateCurrentPosition(PlaylistPlayer playlistPlayer, int position) {
//                Log.d("debug", "---onUpdateCurrentPosition " + position);
                playerModel.getCurrentPosition().setValue(position);
            }

            @Override
            public void onCurrentIndexChange(PlaylistPlayer playlistPlayer, int index) {
                Log.d("debug", "---onCurrentIndexChange " + index);
                playerModel.getCurrentIndex().setValue(index);
            }

            @Override
            public void onCurrentSongChange(PlaylistPlayer playlistPlayer, SongItem song) {
                Log.d("debug", "---onCurrentSongChange " + song.getName());
                playerModel.getCurrentSong().setValue(song);

                playerModel.getCurrentPosition().setValue(0);
                playerModel.getDuration().setValue(song.getDuration());

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(CURRENT_SONG_ID_KEY, song.getId());
                editor.apply();
            }

            @Override
            public void onShuffleChange(PlaylistPlayer playlistPlayer, boolean shuffle) {
                Log.d("debug", "---onShuffleChange " + shuffle);
                playerModel.getShuffle().setValue(shuffle);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(SHUFFLE_KEY, shuffle);
                editor.apply();
            }

            @Override
            public void onRepeatChange(PlaylistPlayer playlistPlayer, int repeat) {
                Log.d("debug", "---onRepeatChange " + repeat);
                playerModel.getRepeat().setValue(repeat);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(REPEAT_KEY, repeat);
                editor.apply();
            }

            @Override
            public void onPlaylistChange(PlaylistPlayer _playlistPlayer, String playlistId, List<SongItem> playlist) {
                Log.d("debug", "---onPlaylistChange " + playlistId);
                playerModel.getTitle().setValue(playlistId);
                playerModel.getItems().setValue(playlist);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(CURRENT_PLAYLIST_ID_KEY, playlistId);
                editor.apply();

                //
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void[] voids) {
                        List<Song> songs = new ArrayList<>();
                        List<SongItem> songItems = playlistPlayer.getPlaylist();
                        int size = songItems.size();
                        for(int i = 0; i < size; i++) {
                            songs.add(new Song(songItems.get(i).getId()));
                        }

                        MySQLite.getInstance(getApplicationContext()).deletePlaylist(CURRENT_PLAYLIST_NAME);
                        MySQLite.getInstance(getApplicationContext()).addPlaylist(CURRENT_PLAYLIST_NAME, songs);

                        return null;
                    }
                }.execute();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("debug", "onStartCommand " + getClass().getSimpleName());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("debug", "onBind " + getClass().getSimpleName());
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("debug", "onUnbind " + getClass().getSimpleName());
        if(!playlistPlayer.isRunning()) {
            Log.d("debug", "stopSelf " + getClass().getSimpleName());
            stopSelf();
        }

        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d("debug", "onRebind " + getClass().getSimpleName());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("debug", "onTaskRemoved " + getClass().getSimpleName());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("debug", "onDestroy " + getClass().getSimpleName());

        playlistPlayer.release();
        playlistPlayer = null;
    }

}
