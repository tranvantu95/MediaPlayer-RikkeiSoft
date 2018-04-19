package com.rikkeisoft.musicplayer.activity.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.util.Log;

import com.rikkeisoft.musicplayer.custom.adapter.SongsRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.SwitchRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.PlaylistModel;
import com.rikkeisoft.musicplayer.model.SongsModel;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.player.PlaylistPlayer;

import java.util.List;

public class PlaylistFragment extends SongsFragment {

    public static PlaylistFragment newInstance(int modelOwner) {
        PlaylistFragment fragment = new PlaylistFragment();

        Bundle args = new Bundle();
        args.putInt("modelOwner", modelOwner);
        fragment.setArguments(args);

        return fragment;
    }

    private PlaylistModel playlistModel;

    private SongItem currentSong;

    private boolean first;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTypeView(SwitchRecyclerAdapter.LIST_VIEW);

        first = true;
    }

    @Override
    protected void onCurrentSongChange(@NonNull SongItem songItem) {
        super.onCurrentSongChange(songItem);
        currentSong = songItem;
        gotoCurrentSong(0);
    }

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(recyclerView != null) {
                int index = recyclerAdapter.getItems().indexOf(currentSong);
                Log.d("debug", "gotoCurrentSong " + index + " PlaylistFragment");
                recyclerView.smoothScrollToPosition(index);
                first = false;
            }
        }
    };

    protected void gotoCurrentSong(int delay) {
        if(currentSong != null && recyclerAdapter.getItems().contains(currentSong) && recyclerView != null) {
            if(delay == 0) runnable.run();
            else {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, delay);
            }
        }
    }

    @Override
    protected void updateRecyclerAdapter(List<SongItem> songItems) {
        super.updateRecyclerAdapter(songItems);

        if(first) gotoCurrentSong(300);
    }

    @Override
    protected void updateRecyclerView() {
        super.updateRecyclerView();

        if(first) gotoCurrentSong(500);
    }

    @Override
    protected SongsRecyclerAdapter onCreateRecyclerAdapter() {
        return new SongsRecyclerAdapter(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Log.d("debug", "---play " + position + " PlaylistFragment");
                if(playlistPlayer != null) playlistPlayer.play(position, playCallback);
            }
        });
    }

    private PlaylistPlayer.PlayCallback playCallback = new PlaylistPlayer.PlayCallback() {
        @Override
        public void onNotReady(PlaylistPlayer playlistPlayer) {}

        @Override
        public void onPreparing(PlaylistPlayer playlistPlayer) {}

        @Override
        public void onPlaying(PlaylistPlayer playlistPlayer) {
            playlistPlayer.pause();
        }

        @Override
        public void onPaused(PlaylistPlayer playlistPlayer) {
            playlistPlayer.start();
        }

        @Override
        public void isNewIndex(PlaylistPlayer playlistPlayer) {
            playlistPlayer.updateCurrentShuffleIndex();
        }
    };

    @Override
    protected SongsModel onCreateModel() {
        playlistModel = getModel(getArguments().getInt("modelOwner"), PlaylistModel.class);
        return playlistModel;
    }

}
