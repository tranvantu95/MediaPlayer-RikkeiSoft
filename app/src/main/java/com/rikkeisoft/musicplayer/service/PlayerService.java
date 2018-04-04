package com.rikkeisoft.musicplayer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.MainActivity;
import com.rikkeisoft.musicplayer.app.MyApplication;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.utils.DBHandler;
import com.rikkeisoft.musicplayer.utils.Loader;
import com.rikkeisoft.musicplayer.utils.PlaylistPlayer;

import java.util.ArrayList;
import java.util.List;

public class PlayerService extends Service {

    public static final String ACTION_KEEP_PLAYER_SERVICE = "com.rikkeisoft.musicplayer.action.KEEP_PLAYER_SERVICE";

    public static final String DATA = "player_data";
    public static final String SHUFFLE_KEY = "shuffle";
    public static final String REPEAT_KEY = "repeat";
    public static final String CURRENT_SONG_ID_KEY = "currentSongId";
    public static final String CURRENT_PLAYLIST_ID_KEY = "currentPlaylistId";
    public static final String CURRENT_POSITION_KEY = "currentPosition";

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

    public class LiveData {

        private MutableLiveData<PlayerModel> playerModel;

        public MutableLiveData<PlayerModel> getPlayerModel() {
            if(playerModel == null) playerModel = new MutableLiveData<>();

            return playerModel;
        }

        private MutableLiveData<PlaylistPlayer> playlistPlayer;

        public MutableLiveData<PlaylistPlayer> getPlaylistPlayer() {
            if(playlistPlayer == null) playlistPlayer = new MutableLiveData<>();

            return playlistPlayer;
        }
    }

    private LiveData liveData;

    public LiveData getLiveData() {
        return liveData;
    }

    private PlaylistPlayer playlistPlayer;

    private PlayerModel playerModel;

    private Notification notification;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("debug", "onCreate " + getClass().getSimpleName());
        startService(new Intent(this, getClass()));

//        playerThread = new HandlerThread("playerThread");
//        playerThread.start();
//
//        playerHandler = new PlayerHandler(playerThread.getLooper());
//        uiHandler = new Handler();

        preferences = getApplicationContext().getSharedPreferences(DATA, Context.MODE_PRIVATE);

        liveData = new LiveData();

        init();

        notification = createNotification();
        startForeground(1001, notification);
    }

    private void init() {
        if(MyApplication.getPlayerModel() == null) {
            new DBHandler.PlaylistLoader(getApplicationContext(), null, new DBHandler.PlaylistLoader.Callback() {
                @Override
                public void onResult(List<SongItem> playlist) {
                    createPlayerModel(playlist);
                }
            }).execute(CURRENT_PLAYLIST_NAME);
        }
        else createPlayerModel(null);
    }

    private void createPlayerModel(List<SongItem> _playlist) {
        String playlistId;
        List<SongItem> playlist;
        SongItem currentSong = null;
        int currentIndex = -1;
        boolean shuffle = false;
        int repeat = PlaylistPlayer.UN_REPEAT;
        boolean play = false;

        if(MyApplication.getPlayerModel() == null) {
            Log.d("debug", "CreatePlayerModel " + getClass().getSimpleName());
            playerModel = new PlayerModel();
            MyApplication.setPlayerModel(playerModel);

            // a little bug (if database incorrect) on song fragment item click
            playlistId = preferences.getString(CURRENT_PLAYLIST_ID_KEY, null);

//            playlist = Loader.getInstance().getSongs(); // test
            playlist = _playlist;

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
            playerModel.getPlaying().setValue(false);

            if(currentSong != null) playerModel.getDuration().setValue(currentSong.getDuration());
            playerModel.getCurrentPosition().setValue(currentSong != null
                    ? preferences.getInt(CURRENT_POSITION_KEY, 0) : 0);
        }
        else {
            Log.d("debug", "RestorePlayerModel " + getClass().getSimpleName());
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

            if(playerModel.getPlaying().getValue() != null)
                play = playerModel.getPlaying().getValue();
        }

        if(liveData.getPlayerModel().getValue() == null
                || !liveData.getPlayerModel().getValue().equals(playerModel)) {
            Log.d("debug", "SetLivePlayerModel " + getClass().getSimpleName());
            liveData.getPlayerModel().setValue(playerModel);
        }

        createPlaylistPlayer(playlistId, playlist, currentSong, currentIndex, shuffle, repeat, play);
    }

    private void createPlaylistPlayer(String playlistId, List<SongItem> playlist, SongItem currentSong,
                                      int currentIndex, boolean shuffle, int repeat, boolean play) {
        Log.d("debug", "CreatePlaylistPlayer " + getClass().getSimpleName());
        playlistPlayer = new PlaylistPlayer(new PlaylistPlayer.Callback() {
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
            public void onCurrentSongChange(PlaylistPlayer playlistPlayer, @Nullable SongItem song) {
                Log.d("debug", "---onCurrentSongChange " + (song != null ? song.getName() : "null"));
                playerModel.getCurrentSong().setValue(song);

                playerModel.getCurrentPosition().setValue(0);
                if(song != null) playerModel.getDuration().setValue(song.getDuration());

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(CURRENT_SONG_ID_KEY, song != null ? song.getId() : -1);
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
                new DBHandler.PlaylistSaver(getApplicationContext(), playlist)
                        .execute(CURRENT_PLAYLIST_NAME);
            }

            @Override
            public void onRelease(PlaylistPlayer playlistPlayer) {
                Log.d("debug", "---onRelease");

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(CURRENT_POSITION_KEY, playlistPlayer.getCurrentPosition());
                editor.apply();
            }

        });
        playlistPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Log.d("debug", "---onError");
                mediaPlayer.release();
                liveData.getPlaylistPlayer().setValue(null);
                MyApplication.setPlaylistPlayer(null);
                init();
                return true;
            }
        });
        playlistPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        playlistPlayer.initialize(playlistId, playlist, currentSong, currentIndex, shuffle, repeat, play);

        liveData.getPlaylistPlayer().setValue(playlistPlayer);
        MyApplication.setPlaylistPlayer(playlistPlayer);
    }

    private Notification createNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "qwerty";

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)  // the status icon
                .setTicker("ticker")  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle("Title")  // the label of the entry
                .setContentText("Content")  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        return notification;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("debug", "onStartCommand " + getClass().getSimpleName());
        return START_STICKY;
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
        if(playlistPlayer != null && !playlistPlayer.isRunning()) {
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

//        sendBroadcast(new Intent(ACTION_KEEP_PLAYER_SERVICE));

//        if(playerModel != null && playerModel.getCurrentPosition().getValue() != null) {
//            Log.d("debug", "save position " + getClass().getSimpleName());
//
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putInt(CURRENT_POSITION_KEY, playerModel.getCurrentPosition().getValue());
//            editor.apply();
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("debug", "onDestroy " + getClass().getSimpleName());

        if(playlistPlayer != null) {
            playlistPlayer.release();
            liveData.getPlaylistPlayer().setValue(null);
            MyApplication.setPlaylistPlayer(null);
        }
    }

}
