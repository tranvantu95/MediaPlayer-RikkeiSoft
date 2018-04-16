package com.rikkeisoft.musicplayer.service;

import android.app.Service;
import android.arch.lifecycle.MutableLiveData;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.rikkeisoft.musicplayer.app.MyApplication;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.player.PlayerNotification;
import com.rikkeisoft.musicplayer.utils.ArrayUtils;
import com.rikkeisoft.musicplayer.player.PlaylistHandler;
import com.rikkeisoft.musicplayer.utils.General;
import com.rikkeisoft.musicplayer.utils.Loader;
import com.rikkeisoft.musicplayer.player.PlaylistPlayer;

import java.util.List;

public class PlayerService extends Service {

    // Media change listener
    private ContentObserver onMediaChange;

    private void registerOnMediaChange() {
        if(onMediaChange != null) return;
        Log.d("debug", "registerOnMediaChange " + getClass().getSimpleName());

        onMediaChange = new ContentObserver(new Handler()) {
            private long latestTime = System.currentTimeMillis();

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);

                if(System.currentTimeMillis() - latestTime > 1000) {
                    latestTime = System.currentTimeMillis();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onMediaChange();
                        }
                    }, 1000);
                }

            }
        };

        getContentResolver().registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, onMediaChange);
    }

    private void unregisterOnMediaChange() {
        if(onMediaChange != null) {
            Log.d("debug", "unregisterOnMediaChange " + getClass().getSimpleName());
            getContentResolver().unregisterContentObserver(onMediaChange);
            onMediaChange = null;
        }
    }

    private void onMediaChange() {
        Log.d("debug", "onMediaChange " + getClass().getSimpleName());
        Loader.getInstance().clearCache();
        reloadPlaylist();
    }

    // Media change listener
    private BroadcastReceiver onReceiverMediaChange;

    private void registerOnReceiverMediaChange() {
        if(onReceiverMediaChange != null) return;
        Log.d("debug", "registerOnReceiverMediaChange " + getClass().getSimpleName());

        onReceiverMediaChange = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onReceiverMediaChange();
            }
        };

        IntentFilter filter = new IntentFilter(MyApplication.ACTION_MEDIA_CHANGE);
        registerReceiver(onReceiverMediaChange, filter);
    }

    private void unregisterOnReceiverMediaChange() {
        if(onReceiverMediaChange != null) {
            Log.d("debug", "unregisterOnReceiverMediaChange " + getClass().getSimpleName());
            unregisterReceiver(onReceiverMediaChange);
            onReceiverMediaChange = null;
        }
    }

    protected void onReceiverMediaChange() {
        Log.d("debug", "onReceiverMediaChange " + getClass().getSimpleName());
        reloadPlaylist();
    }

    // data and database
    private static final String DATA = "player_data";
    private static final String SHUFFLE_KEY = "shuffle";
    private static final String REPEAT_KEY = "repeat";
    private static final String CURRENT_PLAYLIST_NAME_KEY = "currentPlaylistName";
    private static final String CURRENT_SONG_ID_KEY = "currentSongId";
    private static final String CURRENT_POSITION_KEY = "currentPosition";

    public static final String CURRENT_LISTING = "Danh sách phát hiện tại";

    private SharedPreferences preferences;

    // binder
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

    // update for activity
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

    //
    private PlaylistPlayer playlistPlayer;

    public PlaylistPlayer getPlaylistPlayer() {
        return playlistPlayer;
    }

    private PlayerModel playerModel;

    public PlayerModel getPlayerModel() {
        return playerModel;
    }

    //
    private ServiceConnection notificationConnection;
    private NotificationService notificationService;
    private PlayerNotification playerNotification;

    //
    private boolean started, hasConnection;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("debug", "onCreate " + getClass().getSimpleName());

//        registerOnMediaChange();
        registerOnReceiverMediaChange();

//        playerThread = new HandlerThread("playerThread");
//        playerThread.start();
//
//        playerHandler = new PlayerHandler(playerThread.getLooper());
//        uiHandler = new Handler();

        preferences = getApplicationContext().getSharedPreferences(DATA, Context.MODE_PRIVATE);

        liveData = new LiveData();

        init();

    }

    private void init() {
        if(MyApplication.getPlayerModel() == null) {
            if(!General.isPermissionGranted) return;

            new PlaylistHandler.PlaylistLoader(getApplicationContext(), null, new PlaylistHandler.PlaylistLoader.Callback() {
                @Override
                public void onResult(List<SongItem> playlist) {
                    create(playlist);
                }
            }).execute(CURRENT_LISTING);
        }
        else {
            setLivePlayerModel(MyApplication.getPlayerModel());

            if(MyApplication.getPlaylistPlayer() == null) restorePlaylistPlayer();
            else setLivePlaylistPlayer(MyApplication.getPlaylistPlayer());
        }
    }

    private void create(List<SongItem> playlist) {
        String playlistName = preferences.getString(CURRENT_PLAYLIST_NAME_KEY, null);

        int currentSongId = preferences.getInt(CURRENT_SONG_ID_KEY, -1);
        int currentIndex = currentSongId != -1 ? ArrayUtils.findIndex(playlist, currentSongId) : -1;
        SongItem currentSong = currentIndex != -1 ? playlist.get(currentIndex) : null;

        boolean shuffle = preferences.getBoolean(SHUFFLE_KEY, false);
        int repeat = preferences.getInt(REPEAT_KEY, PlaylistPlayer.UN_REPEAT);

        boolean play = false;

        createPlayerModel(playlistName, playlist, currentSong, currentIndex, shuffle, repeat, play);
        createPlaylistPlayer(playlistName, playlist, currentSong, currentIndex, shuffle, repeat, play);
    }

    //
    private void createPlayerModel(String playlistName, List<SongItem> playlist, SongItem currentSong,
                                   int currentIndex, boolean shuffle, int repeat, boolean play) {
        Log.d("debug", "CreatePlayerModel " + getClass().getSimpleName());
        playerModel = new PlayerModel();

        playerModel.getPlaylistName().setValue(playlistName);
        playerModel.getItems().setValue(playlist);
        playerModel.getCurrentSong().setValue(currentSong);
        playerModel.getCurrentIndex().setValue(currentIndex);
        playerModel.getShuffle().setValue(shuffle);
        playerModel.getRepeat().setValue(repeat);
        playerModel.getPlaying().setValue(play);

        if(currentSong != null) playerModel.getDuration().setValue(currentSong.getDuration());
        playerModel.getCurrentPosition().setValue(currentSong != null
                ? preferences.getInt(CURRENT_POSITION_KEY, 0) : 0);

        setLivePlayerModel(playerModel);
    }

    private void createPlaylistPlayer(String playlistName, List<SongItem> playlist, SongItem currentSong,
                                      int currentIndex, boolean shuffle, int repeat, boolean play) {
        Log.d("debug", "CreatePlaylistPlayer " + getClass().getSimpleName());
        playlistPlayer = new PlaylistPlayer(new PlaylistPlayer.Callback() {
            @Override
            public void onPlayingChange(PlaylistPlayer playlistPlayer, boolean playing) {
                Log.d("debug", "---onPlayingChange " + playing);
                playerModel.getPlaying().setValue(playing);

                if(!playing) saveCurrentPosition(playlistPlayer.getCurrentPosition());

                //
                updateNotification();
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

                //
                updateNotification();
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
            public void onPlaylistChange(PlaylistPlayer _playlistPlayer, String playlistName, List<SongItem> playlist) {
                Log.d("debug", "---onPlaylistChange " + playlistName);
                playerModel.getPlaylistName().setValue(playlistName);
                playerModel.getItems().setValue(playlist);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(CURRENT_PLAYLIST_NAME_KEY, playlistName);
                editor.apply();

                //
                new PlaylistHandler.PlaylistSaver(getApplicationContext(), playlist)
                        .execute(CURRENT_LISTING);
            }

            @Override
            public void onNotFoundData(PlaylistPlayer playlistPlayer, SongItem song, int index) {
                Log.d("debug", "---onNotFoundData " + song.getName());
                playlistPlayer.simpleHandleNotFoundData(index);
                Toast.makeText(getApplicationContext(), "NotFoundData " + song.getName(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCompletion(PlaylistPlayer playlistPlayer, SongItem song, int index) {
                Log.d("debug", "---onCompletion " + song.getName());

            }

            @Override
            public void onRelease(PlaylistPlayer playlistPlayer) {
                Log.d("debug", "---onRelease");
                setLivePlaylistPlayer(null);

                saveCurrentPosition(playlistPlayer.getCurrentPosition());
            }

        });
        playlistPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Log.d("debug", "---onError");
                mediaPlayer.release();
                restorePlaylistPlayer();
                return true;
            }
        });
        playlistPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        playlistPlayer.initialize(playlistName, playlist, currentSong, currentIndex, shuffle, repeat, play);

        setLivePlaylistPlayer(playlistPlayer);

        bindNotificationService();
    }

    //
    private void setLivePlayerModel(PlayerModel playerModel) {
        Log.d("debug", "setLivePlayerModel " + getClass().getSimpleName());
        liveData.getPlayerModel().setValue(playerModel);
        MyApplication.setPlayerModel(playerModel);
        this.playerModel = playerModel;
    }

    private void setLivePlaylistPlayer(PlaylistPlayer playlistPlayer) {
        Log.d("debug", "setLivePlaylistPlayer " + (playlistPlayer != null) + " " + getClass().getSimpleName());
        liveData.getPlaylistPlayer().setValue(playlistPlayer);
        MyApplication.setPlaylistPlayer(playlistPlayer);
        this.playlistPlayer = playlistPlayer;
    }

    //
    private void restorePlaylistPlayer() {
        Log.d("debug", "restorePlaylistPlayer " + getClass().getSimpleName());

        String playlistName;
        List<SongItem> playlist;
        SongItem currentSong;
        int currentIndex = -1;
        boolean shuffle = false;
        int repeat = PlaylistPlayer.UN_REPEAT;
        boolean play = false;

        playlistName = playerModel.getPlaylistName().getValue();
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

        createPlaylistPlayer(playlistName, playlist, currentSong, currentIndex, shuffle, repeat, play);
    }

    private void reloadPlaylist() {
        Log.d("debug", "reloadPlaylist " + getClass().getSimpleName());
        new PlaylistHandler.PlaylistLoader(this, playlistPlayer != null ? playlistPlayer.getPlaylist() : null,
                new PlaylistHandler.PlaylistLoader.Callback() {
                    @Override
                    public void onResult(List<SongItem> playlist) {
                        if(playlistPlayer == null) return;
                        int index = playlistPlayer.getCurrentSongId() < 0 ? -1
                                : ArrayUtils.findIndex(playlist, playlistPlayer.getCurrentSongId());
                        playlistPlayer.setPlaylist(playlistPlayer.getPlaylistName(), playlist, index, false);
                    }
                }).execute(PlayerService.CURRENT_LISTING);
    }

    //
    private void saveCurrentPosition(int currentPosition) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(CURRENT_POSITION_KEY, currentPosition);
        editor.apply();
    }

    //
    private void bindNotificationService() {
        if(notificationConnection != null) return;
        Log.d("debug", "bindNotificationService " + getClass().getSimpleName());

        notificationConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                NotificationService.LocalBinder binder = (NotificationService.LocalBinder) service;
                notificationService = binder.getService();
                playerNotification = notificationService.getPlayerNotification();

                // linked playerNotification with NotificationService
                notificationService.startForeground(NotificationService.NOTIFICATION_ID, playerNotification.getNotification());

                // keep this service running when app closed
                startForeground(NotificationService.NOTIFICATION_ID, playerNotification.getNotification());

                if(notificationService.isShowingNotification) updateNotification();
                else notificationService.deleteNotification();

                //
                thisUnbindThis();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {}
        };

        bindService(new Intent(getApplicationContext(), NotificationService.class),
                notificationConnection, BIND_AUTO_CREATE);
    }

    private void unbindNotificationService() {
        if(notificationConnection != null) {
            Log.d("debug", "unbindNotificationService " + getClass().getSimpleName());
            unbindService(notificationConnection);
            notificationConnection = null;
        }
    }

    private void updateNotification() {
        if(notificationService != null && playlistPlayer != null) {
            playerNotification.setSong(playlistPlayer.getCurrentSong());
            playerNotification.setPlay(playlistPlayer.isRunning());

            if(playlistPlayer.getCurrentSong() != null)
                notificationService.showNotification(playlistPlayer.isRunning());
            else notificationService.deleteNotification();
        }
    }

    // PlayerReceiver callback when playerNotification deleted by user
    public void onDeleteNotification() {
        Log.d("debug", "onDeleteNotification " + getClass().getSimpleName());
        if(notificationService != null) notificationService.onDeleteNotification();
        checkStopService();
    }

    private boolean isShowingNotification() {
        return notificationService != null && notificationService.isShowingNotification();
    }

    private boolean isPlaying() {
        return playlistPlayer != null && playlistPlayer.isRunning();
    }

    private void checkStopService() {
        if (!hasConnection && !isPlaying() && !isShowingNotification()) {
            Log.d("debug", "stopSelf " + getClass().getSimpleName());
            stopSelf();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean thisBindThis;
    private ServiceConnection thisConnectionThis = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            thisBindThis = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            thisBindThis = false;
        }
    };

    private void thisStartThis() {
        Log.d("debug", "thisStartThis " + getClass().getSimpleName());
        startService(new Intent(this, getClass()));
    }

    private void thisBindThis() {
        Log.d("debug", "thisBindThis " + getClass().getSimpleName());
        bindService(new Intent(this, getClass()), thisConnectionThis, BIND_AUTO_CREATE);
    }

    public void thisUnbindThis() {
        if(thisBindThis) {
            Log.d("debug", "thisUnbindThis " + getClass().getSimpleName());
            thisBindThis = false;
            unbindService(thisConnectionThis);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("debug", "onStartCommand " + getClass().getSimpleName());
        started = true;

        if(!General.isPermissionGranted) stopSelf(); // when android auto startService
        else if(!hasConnection) thisBindThis(); // for PlayerReceiver peekService

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("debug", "onBind " + getClass().getSimpleName());
        hasConnection = true;

        if(!started) thisStartThis(); // keep service running when onUnbind

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("debug", "onUnbind " + getClass().getSimpleName());
        hasConnection = false;
        checkStopService();
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d("debug", "onRebind " + getClass().getSimpleName());
        hasConnection = true;

        if(MyApplication.getPlayerModel() == null && playerModel != null) {
            MyApplication.setPlayerModel(playerModel);
            MyApplication.setPlaylistPlayer(playlistPlayer);
            reloadPlaylist();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("debug", "onTaskRemoved " + getClass().getSimpleName());
        Toast.makeText(getApplicationContext(), "onTaskRemoved", Toast.LENGTH_LONG).show();

        if(playerModel != null && playerModel.getCurrentPosition().getValue() != null)
            saveCurrentPosition(playerModel.getCurrentPosition().getValue());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("debug", "onDestroy " + getClass().getSimpleName());
        Toast.makeText(getApplicationContext(), "onDestroy " + getClass().getSimpleName(), Toast.LENGTH_LONG).show();

//        unregisterOnMediaChange();
        unregisterOnReceiverMediaChange();

        unbindNotificationService();

        if(playlistPlayer != null) playlistPlayer.release();
    }

}
