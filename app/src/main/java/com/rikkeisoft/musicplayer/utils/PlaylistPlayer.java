package com.rikkeisoft.musicplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.util.Log;

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

    private boolean running;

    private Handler handler;
    private Runnable updateCurrentPosition;

    private boolean preparing, ready;

    private int repeat;
    private boolean shuffle;

    private int currentIndex, currentId;

    private List<SongItem> playlist = new ArrayList<>();

    private List<SongItem> shuffleList;
    private int currentShuffleIndex;

    public void setPlaylist(List<SongItem> playlist, int index, boolean play) {
        if(playlist != null) {
            int currentSongId = getCurrentSongId();

            this.playlist = playlist;

            if(shuffle) createShuffleList(index);

            currentIndex = -1;

            if(play) {
                if (currentSongId == playlist.get(index).getId()) {
                    setCurrentIndex(index);
                    resume();
                }
                else {
                    play(index, null);
                }
            }
            else setCurrentIndex(index);
        }
    }

    private PlayerModel playerModel;

    private SharedPreferences preferences;

    public PlaylistPlayer(@NonNull Context context, @NonNull PlayerModel _playerModel) {
        super();

        playerModel = _playerModel;

        setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);

        preferences = context.getSharedPreferences(DATA, Context.MODE_PRIVATE);

        setShuffle(preferences.getBoolean(SHUFFLE_KEY, false));
        setRepeat(preferences.getInt(REPEAT_KEY, PlaylistPlayer.UN_REPEAT));

        int currentSongId = preferences.getInt(CURRENT_SONG_ID_KEY, -1);
        if(currentSongId != -1) {
            int currentIndex = Loader.findIndex(Loader.getInstance().getSongs(), currentSongId);
            if(currentIndex != -1) setCurrentIndex(currentIndex);
        }

        playerModel.getCurrentPosition().setValue(0);
        playerModel.getPlaying().setValue(false);

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

    private String getPath(int index) {
        return playlist.get(index).getPath();
    }

    private void setPlaying(boolean playing) {
        if(playerModel.getPlaying().getValue() != null
                && playerModel.getPlaying().getValue() == playing) return;
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
            if(!running) setPlaying(false);
        }
        else {
            playerModel.getDuration().setValue(getDuration());
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

        if(song != null && (playerModel.getCurrentSongId().getValue() == null
                || playerModel.getCurrentSongId().getValue() != song.getId())) {
            playerModel.getCurrentSongId().setValue(song.getId());
            playerModel.getCurrentAlbumId().setValue(song.getAlbumId());
            playerModel.getCurrentArtistId().setValue(song.getArtistId());

            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(CURRENT_SONG_ID_KEY, getCurrentSongId());
            editor.apply();
        }
    }

    @Override
    public void release() {
        super.release();
        stopUpdateCurrentPosition();
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
        running = false;
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        setPlaying(true);
        running = true;
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

    @Override
    public void seekTo(int msec) throws IllegalStateException {
        super.seekTo(msec);
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

        running = true;

        prepare(index);
    }

    public interface PlayCallback {
        void onIsPlaying(PlaylistPlayer playlistPlayer);
    }

    private PlayCallback rePlayOnIsPlaying = new PlayCallback() {
        @Override
        public void onIsPlaying(PlaylistPlayer playlistPlayer) {
            playlistPlayer.replay();
        }
    };

    private int getShuffleIndex(int index) {
        currentShuffleIndex = index;
        return playlist.indexOf(shuffleList.get(index));
    }

    private void playShuffle(int index) {
        play(getShuffleIndex(index), rePlayOnIsPlaying);
    }

    public void next() {
        next(true);
    }

    private void next(boolean fromUser) {
        if(playlist.isEmpty()) return;

        if(repeat == REPEAT_SONG) {
            replay();
            return;
        }

        if(!shuffle) {
            if(currentIndex < playlist.size() - 1) {
                play(currentIndex + 1, rePlayOnIsPlaying);
            }
            else if(fromUser || repeat == REPEAT_PLAYLIST) {
                play(0, rePlayOnIsPlaying);
            }
            else {
                setCurrentIndex(0);
                running = false;
                reset();
            }
        }
        else {
            if(currentShuffleIndex < shuffleList.size() - 1) {
                playShuffle(currentShuffleIndex + 1);
            }
            else if(fromUser || repeat == REPEAT_PLAYLIST) {
                Collections.shuffle(shuffleList);
                playShuffle(0);
            }
            else {
                Collections.shuffle(shuffleList);
                setCurrentIndex(getShuffleIndex(0));
                running = false;
                reset();
            }
        }
    }

    public void previous() {
        if(playlist.isEmpty()) return;

        if(repeat == REPEAT_SONG) {
            replay();
            return;
        }

        if(getCurrentPosition() > 3000) replay();
        else if(!shuffle) {
            if(currentIndex > 0) {
                play(currentIndex - 1, rePlayOnIsPlaying);
            }
            else {
                play(playlist.size() - 1, rePlayOnIsPlaying);
            }
        }
        else {
            if(currentShuffleIndex > 0) {
                playShuffle(currentShuffleIndex - 1);
            }
            else {
                playShuffle(shuffleList.size() - 1);
            }
        }
    }

    public void replay() {
        if(!ready) play(currentIndex, null);
        else {
            seekTo(0);
            if(!isPlaying()) start();
        }
    }

    public void resume() {
        if(!ready) play(currentIndex, null);
        else if(!isPlaying()) start();
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

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
        if(shuffle) createShuffleList(currentIndex);
        else shuffleList = null;
        playerModel.getShuffle().setValue(shuffle);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SHUFFLE_KEY, shuffle);
        editor.apply();
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
        setLooping(repeat == REPEAT_SONG);
        playerModel.getRepeat().setValue(repeat);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(REPEAT_KEY, repeat);
        editor.apply();
    }

    private void createShuffleList(int index) {
        shuffleList = new ArrayList<>(playlist);
        Collections.shuffle(shuffleList);
        if(isValidateIndex(playlist, index))
            currentShuffleIndex = shuffleList.indexOf(playlist.get(index));
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

    public SongItem getCurrentSong() {
        if(!isValidateIndex(playlist, currentIndex)) return null;
        return playlist.get(currentIndex);
    }

    public int getCurrentSongId() {
        if(!isValidateCurrentIndex()) return -1;
        return playlist.get(currentIndex).getId();
    }

    private boolean isValidateCurrentIndex() {
        return isValidateIndex(playlist, currentIndex);
    }

    private boolean isValidateIndex(List list, int index) {
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
