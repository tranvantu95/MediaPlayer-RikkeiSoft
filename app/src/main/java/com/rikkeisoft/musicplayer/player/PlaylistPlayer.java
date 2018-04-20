package com.rikkeisoft.musicplayer.player;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.utils.ArrayUtils;
import com.rikkeisoft.musicplayer.utils.Loader;

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

    private String playlistName;

    public String getPlaylistName() {
        return playlistName;
    }

    private List<SongItem> playlist = new ArrayList<>();

    public List<SongItem> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(String playlistName, List<SongItem> playlist, int index, boolean mustPlay) {
        if(playlist != null) {

            this.playlistName = playlistName;

            int oldSongId = getCurrentSongId();

            this.playlist = playlist;

            callback.onPlaylistChange(this, playlistName, playlist);

            setCurrentIndex(index, true);

            if(shuffle) createShuffleList();

            if(mustPlay) {
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

    public void setPlaylist(String playlistName, List<SongItem> playlist, SongItem song, boolean mustPlay) {
        if(playlist != null) {

            this.playlistName = playlistName;

            int oldSongId = getCurrentSongId();

            this.playlist = playlist;

            callback.onPlaylistChange(this, playlistName, playlist);

            setCurrentSong(song, true);

            if(shuffle) createShuffleList();

            if(mustPlay) {
                if (getCurrentSongId() == oldSongId && oldSongId != -1) {
                    resume();
                }
                else {
                    setReady(false);
                    play(song, null);
                }
            }

        }
    }

    private Callback callback;

    public PlaylistPlayer(Callback _callback) {
        super();

        callback = _callback;

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
                if(running) start();
                setReady(true);
            }
        });

        setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                callback.onCompletion(PlaylistPlayer.this, currentSong, currentIndex);
                if(repeat == REPEAT_SONG) start();
                else next(false);
            }
        });
    }

    public void initialize(String playlistId, List<SongItem> playlist, SongItem currentSong,
                           int currentIndex, boolean shuffle, int repeat, boolean play) {

        this.playlistName = playlistId;
        if(playlist != null) this.playlist = playlist;

        this.currentSong = currentSong;
        this.currentIndex = currentIndex;

        this.shuffle = shuffle;
        if(shuffle) createShuffleList();

        this.repeat = repeat;

        if(play) play(currentIndex, null);
        else if(isValidateCurrentIndex()) prepare(currentIndex);
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

        callback.onReadyChange(this, ready);
    }

    private void setCurrentIndex(int index, boolean b) {
        if(currentIndex != index) {
            currentIndex = index;
            callback.onCurrentIndexChange(this, index);
        }

        if(b) setCurrentSong(findCurrentSong(), false);
    }

    private void setCurrentSong(SongItem song, boolean b) {
        if(currentSong == null && song != null || currentSong != null && !currentSong.equals(song)) {
            currentSong = song;
            callback.onCurrentSongChange(this, song);

            if(song == null) stop();
        }

        if(b) setCurrentIndex(findCurrentIndex(), false);
    }

    @Override
    public void release() {
        callback.onRelease(this);
        stopUpdateCurrentPosition();
        super.release();
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
        catch (Exception ex) {
            ex.printStackTrace();
            callback.onNotFoundData(this, playlist.get(index), index);
        }
    }

    private void prepare(SongItem song) {
        reset();
        try {
            setDataSource(song.getPath());
            prepare();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            callback.onNotFoundData(this, song, playlist.indexOf(song));
        }
    }

    public void simpleHandleNotFoundData(int index) {
        playlist.remove(index);
        setPlaylist(playlistName, playlist, index, true);
    }


    public void play(int index, PlayCallback callback) {
        if(!ArrayUtils.isValidateIndex(playlist, index)) {
            Log.d("debug", "index out of bound " + getClass().getSimpleName());
            return;
        }

        setPlaying(true);

        if(index == currentIndex) {
            if(preparing) {
                if(callback != null) callback.onPreparing(this);
                return;
            }
            else if(ready) {
                if (isPlaying()) {
                    if(callback != null) callback.onPlaying(this);
                }
                else {
                    if(callback != null) callback.onPaused(this);
                    else start();
                }
                return;
            }
            else {
                if(callback != null) callback.onNotReady(this);
                prepare(index);
                return;
            }
        }

        prepare(index);

        setCurrentIndex(index, true);

        if(callback != null) callback.isNewIndex(this);
    }

    public void play(SongItem song, PlayCallback callback) {
        if(song == null) {
            Log.d("debug", "song is null " + getClass().getSimpleName());
            return;
        }

        if(song.equals(currentSong)) {
            if(preparing) {
                if(callback != null) callback.onPreparing(this);
                return;
            }
            else if(ready) {
                if (isPlaying()) {
                    if(callback != null) callback.onPlaying(this);
                }
                else if(callback != null) callback.onPaused(this);
                else start();
                return;
            }
            else if(callback != null) callback.onNotReady(this);
        }

        setCurrentSong(song, true);

        setPlaying(true);

        prepare(song);
    }

    public interface PlayCallback {
        void onNotReady(PlaylistPlayer playlistPlayer);
        void onPreparing(PlaylistPlayer playlistPlayer);
        void onPlaying(PlaylistPlayer playlistPlayer);
        void onPaused(PlaylistPlayer playlistPlayer);
        void isNewIndex(PlaylistPlayer playlistPlayer);
    }

    private PlayCallback playCallback = new PlayCallback() {
        @Override
        public void onNotReady(PlaylistPlayer playlistPlayer) {
            playlistPlayer.seekTo(0);
        }

        @Override
        public void onPreparing(PlaylistPlayer playlistPlayer) {}

        @Override
        public void onPlaying(PlaylistPlayer playlistPlayer) {
            playlistPlayer.replay();
        }

        @Override
        public void onPaused(PlaylistPlayer playlistPlayer) {
            playlistPlayer.replay();
        }

        @Override
        public void isNewIndex(PlaylistPlayer playlistPlayer) {}
    };

    private int getRandomIndex() {
        return (int) (Math.random() * playlist.size());
    }

    private int getShuffleIndex(int index) {
        return shuffleList.indexOf(playlist.get(index));
    }

    private int getIndex(int shuffleIndex) {
        return playlist.indexOf(shuffleList.get(shuffleIndex));
    }

    public void updateCurrentShuffleIndex() {
        if(shuffle) currentShuffleIndex = getShuffleIndex(currentIndex);
    }

    private void playShuffle(int shuffleIndex, PlayCallback playCallback) {
        if(!ArrayUtils.isValidateIndex(shuffleList, shuffleIndex)) {
            Log.d("debug", "index out of bound " + getClass().getSimpleName());
            return;
        }

        currentShuffleIndex = shuffleIndex;

        play(getIndex(shuffleIndex), playCallback);
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
//                int index = getIndex(0);
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

    public interface TogglePlayCallback {
        void onPlaylistEmpty(PlaylistPlayer playlistPlayer, List<SongItem> playlist);
    }

    public void handlePlaylistEmpty(String playlistName, List<SongItem> playlist, boolean play) {
        if(playlist == null || playlist.isEmpty()) return;

        this.playlist.addAll(playlist);
        int index = !play ? -1 : !shuffle ? 0 : getRandomIndex();
        setPlaylist(playlistName, this.playlist, index, play);
    }

    public void handlePlaylistEmptyDefault(boolean play) {
        if(Loader.getInstance().isLoaded())
            handlePlaylistEmpty("Beauty Music", Loader.getInstance().getSongs(), play);
    }

    public void togglePlay(boolean fixIndex, TogglePlayCallback togglePlayCallback) {
        if(!running) {
            if(fixIndex) {
                if(playlist.isEmpty()) {
                    if(togglePlayCallback == null) handlePlaylistEmptyDefault(true);
                    else togglePlayCallback.onPlaylistEmpty(this, playlist);

                    return;
                }

                if(!isValidateCurrentIndex()) {
                    if(!shuffle) play(0, null);
                    else playShuffle(0, null);

                    return;
                }
            }

            resume();
        }
        else pause();
    }

    public void togglePlay() {
        togglePlay(true, null);
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

        callback.onRepeatChange(this, repeat);
    }

    private void createShuffleList() {
        shuffleList = new ArrayList<>(playlist);
        Collections.shuffle(shuffleList);

        if(isValidateCurrentIndex())
            currentShuffleIndex = getShuffleIndex(currentIndex);
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

    public boolean isShuffle() {
        return shuffle;
    }

    public int getRepeat() {
        return repeat;
    }

    //
    public boolean canSeekTo(int position) {
        return !(isLastSong() && repeat == UN_REPEAT && position > getDuration() - 3000);
    }

    public boolean isLastSong() {
        return currentIndex == playlist.size() - 1;
    }

    //
    public int getCurrentIndex() {
        return currentIndex;
    }

    public SongItem getCurrentSong() {
        return currentSong;
    }

    public int getCurrentSongId() {
        return currentSong != null ? currentSong.getId() : -1;
    }

    //
    public int findCurrentIndex() {
        return playlist.indexOf(currentSong);
    }

    public SongItem findCurrentSong() {
        if(!isValidateCurrentIndex()) return null;
        return playlist.get(currentIndex);
    }

    public int findCurrentSongId() {
        if(!isValidateCurrentIndex()) return -1;
        return playlist.get(currentIndex).getId();
    }

    //
    private boolean isValidateCurrentIndex() {
        return ArrayUtils.isValidateIndex(playlist, currentIndex);
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
        void onCurrentSongChange(PlaylistPlayer playlistPlayer, @Nullable SongItem song);
        void onShuffleChange(PlaylistPlayer playlistPlayer, boolean shuffle);
        void onRepeatChange(PlaylistPlayer playlistPlayer, int repeat);
        void onPlaylistChange(PlaylistPlayer playlistPlayer, String playlistId, List<SongItem> playlist);
        void onNotFoundData(PlaylistPlayer playlistPlayer, SongItem song, int index);
        void onCompletion(PlaylistPlayer playlistPlayer, SongItem song, int index);
        void onRelease(PlaylistPlayer playlistPlayer);
    }
}
