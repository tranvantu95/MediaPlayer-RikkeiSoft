package com.rikkeisoft.musicplayer.utils;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.rikkeisoft.musicplayer.db.AppDatabase;
import com.rikkeisoft.musicplayer.db.Song;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.item.SongItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaylistPlayer extends MediaPlayer {

    public static final String DATA = "player_data";
    public static final String SHUFFLE_KEY = "shuffle";
    public static final String REPEAT_KEY = "repeat";
    public static final String CURRENT_SONG_ID_KEY = "currentSongId";

    public static final int UN_REPEAT = 0;
    public static final int REPEAT_PLAYLIST = 1;
    public static final int REPEAT_SONG = 2;

    private boolean firstPlay = true;

    private boolean running;

    public boolean isRunning() {
        return running;
    }

    private Handler handler;
    private Runnable updateCurrentPosition;

    private boolean preparing, ready;

    private int repeat;
    private boolean shuffle;

    private List<SongItem> shuffleList;
    private int currentShuffleIndex;

    private int currentIndex = -1;

    private String playlistId;

    public String getPlaylistId() {
        return playlistId;
    }

    private List<SongItem> playlist = new ArrayList<>();

    public List<SongItem> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(String playlistId, List<SongItem> playlist, int index, boolean play) {
        if(playlist != null) {
            this.playlistId = playlistId;
            playerModel.getTitle().setValue(playlistId);

            int oldSongId = getCurrentSongId();

            this.playlist = playlist;

            currentIndex = -1;
            setCurrentIndex(index);

            if(shuffle) createShuffleList();

            if(play) {
                if (oldSongId == getCurrentSongId()) {
                    resume();
                }
                else {
                    preparing = ready = false;
                    play(index, null);
                }
            }

            //
//            final Song[] songs = new Song[playlist.size()];
//            for(int i = 0; i < playlist.size(); i++) {
//                songs[i] = new Song(playlist.get(i).getId());
//            }
//                    db.songDao().deleteAll();
//                    db.songDao().insertAll(songs);

        }
    }

    private PlayerModel playerModel;

    public PlayerModel getPlayerModel() {
        return playerModel;
    }

    private SharedPreferences preferences;

    private AppDatabase db;

    public PlaylistPlayer(Context context, PlayerModel _playerModel) {
        super();

        setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);

        preferences = context.getSharedPreferences(DATA, Context.MODE_PRIVATE);

        if(_playerModel != null) {
            playerModel = _playerModel;

            if(playerModel.getItems().getValue() != null)
                playlist = playerModel.getItems().getValue();

            if(playerModel.getCurrentIndex().getValue() != null)
                currentIndex = playerModel.getCurrentIndex().getValue();

        }
        else {
            playerModel = new PlayerModel();

            // load database
//            db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "app-database").build();

            playlist = Loader.getInstance().getSongs(); // test
            playerModel.getItems().setValue(playlist);

            int currentSongId = preferences.getInt(CURRENT_SONG_ID_KEY, -1);
            if(currentSongId != -1) {
                int currentIndex = Loader.findIndex(playlist, currentSongId);
                if(currentIndex != -1) setCurrentIndex(currentIndex);
            }

            playerModel.getCurrentPosition().setValue(0);
        }

        if(isValidateCurrentIndex()) prepare(currentIndex);

        setupShuffle(preferences.getBoolean(SHUFFLE_KEY, false));
        setupRepeat(preferences.getInt(REPEAT_KEY, PlaylistPlayer.UN_REPEAT));

        playerModel.getPlaying().setValue(false);

        //
        handler = new Handler();

        updateCurrentPosition = new Runnable() {
            @Override
            public void run() {
                playerModel.getCurrentPosition().setValue(getCurrentPosition());
                handler.postDelayed(this, 100);
            }
        };

//        setOnErrorListener(new OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//                // prepareAsync with url
//                return false;
//            }
//        });

//        setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
//            @Override
//            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
//                // prepareAsync with url
//            }
//        });

        setOnSeekCompleteListener(new OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mediaPlayer) {
                playerModel.getCurrentPosition().setValue(getCurrentPosition());
            }
        });

        setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                setReady(true);
            }
        });

        setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                next(false);
            }
        });
    }

    private void setPlaying(boolean playing) {
        if(running == playing) return;
        running = playing;

        playerModel.getPlaying().setValue(playing);

        if(playing) startUpdateCurrentPosition();
        else stopUpdateCurrentPosition();
    }

    private void setReady(boolean ready) {
        if(this.ready == ready) return;
        this.ready = ready;
        preparing = false;
        if(!ready) {
            playerModel.getCurrentPosition().setValue(0);
        }
        else {
            playerModel.getDuration().setValue(getDuration());

            if(playerModel.getCurrentPosition().getValue() != null)
                seekTo(playerModel.getCurrentPosition().getValue());

            if(running) start();
        }
    }

    private void setCurrentIndex(int index) {
        if(currentIndex == index) return;
        currentIndex = index;

        if(playerModel.getCurrentIndex().getValue() == null
                || playerModel.getCurrentIndex().getValue() != index)
            playerModel.getCurrentIndex().setValue(currentIndex);

        SongItem song = getCurrentSong();

        if(song != null && !song.equals(playerModel.getCurrentSong().getValue())) {
            playerModel.getCurrentSong().setValue(song);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(CURRENT_SONG_ID_KEY, song.getId());
            editor.apply();
        }
    }

    @Override
    public void release() {
        super.release();
        setPlaying(false);
    }

    @Override
    public void reset() {
        super.reset();
        setReady(false);
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        setReady(false);
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        setPlaying(false);
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        setPlaying(true);
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        super.prepareAsync();
        preparing = true;
    }

    @Override
    public void prepare() throws IOException, IllegalStateException {
        super.prepare();
        preparing = true;
    }

    private String getPath(int index) {
        return playlist.get(index).getPath();
    }

    private void prepare(int index) {
        reset();
        try {
            setDataSource(getPath(index));
            prepare();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void play(int index, PlayCallback callback) {
        if(!isValidateIndex(playlist, index)) {
            Log.d("debug", "index out of bound " + getClass().getSimpleName());
            return;
        }

        if(currentIndex == index) {
            if(preparing) return;
            if(ready) {
                if(!isPlaying()) start();
                else if(callback != null) callback.onIsPlaying(this);
                return;
            }
        }

        setCurrentIndex(index);

        setPlaying(true);

        prepare(index);
    }

    public interface PlayCallback {
        void onIsPlaying(PlaylistPlayer playlistPlayer);
    }

    private PlayCallback playCallback = new PlayCallback() {
        @Override
        public void onIsPlaying(PlaylistPlayer playlistPlayer) {
            playlistPlayer.replay();
        }
    };

    private int getShuffleIndex(int index) {
        currentShuffleIndex = index;
        return playlist.indexOf(shuffleList.get(index));
    }

    private void playShuffle(int index, PlayCallback playCallback) {
        if(!isValidateIndex(shuffleList, index)) {
            Log.d("debug", "index out of bound " + getClass().getSimpleName());
            return;
        }

        play(getShuffleIndex(index), playCallback);
    }

    public void next() {
        next(true);
    }

    private void next(boolean fromUser) {
        if(repeat == REPEAT_SONG) {
            replay();
            return;
        }

        if(!shuffle) {
            if(currentIndex < playlist.size() - 1) {
                play(currentIndex + 1, playCallback);
            }
            else if(fromUser || repeat == REPEAT_PLAYLIST) {
                play(0, playCallback);
            }
            else {
                setPlaying(false);
//                setCurrentIndex(0);
//                prepare(0);
            }
        }
        else {
            if(currentShuffleIndex < shuffleList.size() - 1) {
                playShuffle(currentShuffleIndex + 1, playCallback);
            }
            else if(fromUser || repeat == REPEAT_PLAYLIST) {
                Collections.shuffle(shuffleList);
                playShuffle(0, playCallback);
            }
            else {
                setPlaying(false);
//                Collections.shuffle(shuffleList);
//                int index = getShuffleIndex(0);
//                setCurrentIndex(index);
//                prepare(index);
            }
        }
    }

    public void previous() {
        if(repeat == REPEAT_SONG) {
            replay();
            return;
        }

        if(getCurrentPosition() > 3000) replay();
        else if(!shuffle) {
            if(currentIndex > 0) {
                play(currentIndex - 1, playCallback);
            }
            else {
                play(playlist.size() - 1, playCallback);
            }
        }
        else {
            if(currentShuffleIndex > 0) {
                playShuffle(currentShuffleIndex - 1, playCallback);
            }
            else {
                playShuffle(shuffleList.size() - 1, playCallback);
            }
        }
    }

    public void resume() {
        if(!ready) play(currentIndex, null);
        else if(!isPlaying()) start();
    }

    public void replay() {
        if(!ready) play(currentIndex, null);
        else {
            seekTo(0);
            if(!isPlaying()) start();
        }
    }

    public void togglePlay() {
        if(!ready) play(currentIndex, null);
        else if(!isPlaying()) start();
        else pause();
    }

    public void toggleShuffle() {
        setShuffle(!shuffle);
    }

    public void toggleRepeat() {
        switch (repeat) {
            case UN_REPEAT:
                setRepeat(REPEAT_PLAYLIST);
                break;

            case REPEAT_PLAYLIST:
                setRepeat(REPEAT_SONG);
                break;

            case REPEAT_SONG:
                setRepeat(UN_REPEAT);
                break;
        }
    }

    private void setupShuffle(boolean shuffle) {
        this.shuffle = shuffle;
        if(shuffle) createShuffleList();
        else shuffleList = null;
        playerModel.getShuffle().setValue(shuffle);
    }

    public void setShuffle(boolean shuffle) {
        setupShuffle(shuffle);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SHUFFLE_KEY, shuffle);
        editor.apply();
    }

    private void setupRepeat(int repeat) {
        this.repeat = repeat;
        setLooping(repeat == REPEAT_SONG);
        playerModel.getRepeat().setValue(repeat);
    }

    public void setRepeat(int repeat) {
        setupRepeat(repeat);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(REPEAT_KEY, repeat);
        editor.apply();
    }

    private void createShuffleList() {
        shuffleList = new ArrayList<>(playlist);
        Collections.shuffle(shuffleList);
        if(isValidateCurrentIndex())
            currentShuffleIndex = shuffleList.indexOf(playlist.get(currentIndex));
    }

    //
    public boolean isShuffle() {
        return shuffle;
    }

    public int getRepeat() {
        return repeat;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getCurrentSongId() {
        if(!isValidateCurrentIndex()) return -1;
        return playlist.get(currentIndex).getId();
    }

    public SongItem getCurrentSong() {
        if(!isValidateCurrentIndex()) return null;
        return playlist.get(currentIndex);
    }

    private boolean isValidateCurrentIndex() {
        return isValidateIndex(playlist, currentIndex);
    }

    public static boolean isValidateIndex(List list, int index) {
        return index >= 0 && index < list.size();
    }

    //
    private void startUpdateCurrentPosition() {
        handler.removeCallbacks(updateCurrentPosition);
        handler.postDelayed(updateCurrentPosition, 100);
    }

    private void stopUpdateCurrentPosition() {
        handler.removeCallbacks(updateCurrentPosition);
    }

}
