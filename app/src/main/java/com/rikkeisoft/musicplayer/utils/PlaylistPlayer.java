package com.rikkeisoft.musicplayer.utils;

import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import com.rikkeisoft.musicplayer.model.item.SongItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaylistPlayer extends MediaPlayer {

    public static final int UN_REPEAT = 0;
    public static final int REPEAT_PLAYLIST = 1;
    public static final int REPEAT_SONG = 2;

    private boolean running;

    private Handler handler;
    private Runnable updateCurrentPosition;

    private boolean preparing, ready;

    private int repeat;
    private boolean shuffle;

    private List<SongItem> shuffleList;
    private int currentShuffleIndex;

    private int currentIndex = -1;
    private SongItem currentSong;

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

            int oldSongId = getCurrentSongId();

            this.playlist = playlist;

            callback.onPlaylistChange(this, playlistId, playlist);

            setCurrentIndex(index);

            if(shuffle) createShuffleList();

            if(play) {
                if (getCurrentSongId() == oldSongId && oldSongId != -1) {
                    resume();
                }
                else {
                    setReady(false);
                    play(index, null);
                }
            }

        }
    }

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public PlaylistPlayer() {
        super();

        //
        handler = new Handler();

        updateCurrentPosition = new Runnable() {
            @Override
            public void run() {
                if(ready) callback.onUpdateCurrentPosition(PlaylistPlayer.this, getCurrentPosition());
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

    public void initialize(String playlistId, List<SongItem> playlist, SongItem currentSong,
                           int currentIndex, boolean shuffle, int repeat) {

        this.playlistId = playlistId;
        if(playlist != null) this.playlist = playlist;

        this.currentSong = currentSong;
        this.currentIndex = currentIndex;

        this.shuffle = shuffle;
        if(shuffle) createShuffleList();

        this.repeat = repeat;
        if(repeat == REPEAT_SONG) setLooping(true);

        if(isValidateCurrentIndex()) prepare(currentIndex);
    }

    private void setPlaying(boolean playing) {
        if(running == playing) return;
        running = playing;

        if(playing) startUpdateCurrentPosition();
        else stopUpdateCurrentPosition();

        callback.onPlayingChange(this, playing);
    }

    private void setReady(boolean ready) {
        if(this.ready == ready) return;
        this.ready = ready;

        preparing = false;

        if(ready && running) start();

        callback.onReadyChange(this, ready);
    }

    private void setCurrentIndex(int index) {
        if(currentIndex != index) {
            currentIndex = index;
            callback.onCurrentIndexChange(this, index);
        }

        SongItem song = getCurrentSong();

        if(currentSong == null && song != null || currentSong != null && !currentSong.equals(song)) {
            currentSong = song;
            callback.onCurrentSongChange(this, song);
        }

        if(song == null) stop();
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
        setPlaying(false);
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

    @Override
    public void seekTo(int msec) {
        if(ready) super.seekTo(msec);
        if(!isPlaying()) callback.onUpdateCurrentPosition(this, msec);
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

        if(index == currentIndex) {
            if(preparing) {
                if(callback != null) callback.onPreparing(this);
                return;
            }
            else if(ready) {
                if(callback != null) {
                    if (isPlaying()) callback.onPlaying(this);
                    else callback.onPaused(this);
                }
                return;
            }
            else if(callback != null) callback.onNotReady(this);
        }

        setCurrentIndex(index);

        setPlaying(true);

        prepare(index);
    }

    public interface PlayCallback {
        void onNotReady(PlaylistPlayer playlistPlayer);
        void onPreparing(PlaylistPlayer playlistPlayer);
        void onPlaying(PlaylistPlayer playlistPlayer);
        void onPaused(PlaylistPlayer playlistPlayer);
    }

    private PlayCallback playCallback = new PlayCallback() {
        @Override
        public void onNotReady(PlaylistPlayer playlistPlayer) {
            playlistPlayer.seekTo(0);
        }

        @Override
        public void onPreparing(PlaylistPlayer playlistPlayer) {

        }

        @Override
        public void onPlaying(PlaylistPlayer playlistPlayer) {
            playlistPlayer.replay();
        }

        @Override
        public void onPaused(PlaylistPlayer playlistPlayer) {
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
//        if(repeat == REPEAT_SONG) {
//            replay();
//            return;
//        }

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
//        if(repeat == REPEAT_SONG) {
//            replay();
//            return;
//        }

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
        seekTo(0);
        resume();
    }

    public void togglePlay() {
        if(!running) resume();
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
        if(this.shuffle == shuffle) return;
        this.shuffle = shuffle;

        if(shuffle) createShuffleList();
        else shuffleList = null;

        callback.onShuffleChange(this, shuffle);
    }

    public void setRepeat(int repeat) {
        if(this.repeat == repeat) return;
        this.repeat = repeat;

        setLooping(repeat == REPEAT_SONG);

        callback.onRepeatChange(this, repeat);
    }

    private void createShuffleList() {
        shuffleList = new ArrayList<>(playlist);
        Collections.shuffle(shuffleList);

        if(isValidateCurrentIndex())
            currentShuffleIndex = shuffleList.indexOf(playlist.get(currentIndex));
        else
            currentShuffleIndex = -1;
    }

    //
    public boolean isReady() {
        return ready;
    }

    public boolean isRunning() {
        return running;
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

    //
    public interface Callback {
        void onPlayingChange(PlaylistPlayer playlistPlayer, boolean playing);
        void onReadyChange(PlaylistPlayer playlistPlayer, boolean ready);
        void onUpdateCurrentPosition(PlaylistPlayer playlistPlayer, int position);
        void onCurrentIndexChange(PlaylistPlayer playlistPlayer, int index);
        void onCurrentSongChange(PlaylistPlayer playlistPlayer, SongItem song);
        void onShuffleChange(PlaylistPlayer playlistPlayer, boolean shuffle);
        void onRepeatChange(PlaylistPlayer playlistPlayer, int repeat);
        void onPlaylistChange(PlaylistPlayer playlistPlayer, String playlistId, List<SongItem> playlist);
    }
}
