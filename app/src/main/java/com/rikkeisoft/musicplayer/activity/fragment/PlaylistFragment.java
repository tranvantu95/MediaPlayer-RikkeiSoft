package com.rikkeisoft.musicplayer.activity.fragment;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.util.Log;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.PlayerActivity;
import com.rikkeisoft.musicplayer.activity.base.BaseListFragment;
import com.rikkeisoft.musicplayer.app.MyApplication;
import com.rikkeisoft.musicplayer.custom.adapter.SongsRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.SwitchRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.view.MyLinearLayoutManager;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.PlaylistModel;
import com.rikkeisoft.musicplayer.model.SongsModel;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.utils.PlaylistPlayer;

import java.util.List;

public class PlaylistFragment extends SongsFragment {

    public static PlaylistFragment newInstance(int modelOwner) {
        PlaylistFragment fragment = new PlaylistFragment();

        Bundle args = new Bundle();
        args.putInt("modelOwner", modelOwner);
        fragment.setArguments(args);

        return fragment;
    }

    private MyLinearLayoutManager layoutManager;
    private PlaylistModel playlistModel;

    private int currentIndex = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTypeView(SwitchRecyclerAdapter.LIST_VIEW);

        playlistModel.getTop().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer != null) layoutManager.offsetBot = integer;
            }
        });
    }

    @Override
    protected void playerModelObserve(PlayerModel playerModel) {
        super.playerModelObserve(playerModel);

        playerModel.getCurrentIndex().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer != null) gotoPos(integer);
            }
        });
    }

    private void gotoPos(Integer position) {
        currentIndex = position;
        if(position >= 0 && position < recyclerAdapter.getItemCount() && recyclerView != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("debug", "gotoPos " + currentIndex + " PlaylistFragment");
                    recyclerView.smoothScrollToPosition(currentIndex);
                }
            }, 300);
        }
    }

    @Override
    protected void updateRecyclerAdapter(List<SongItem> songItems) {
        super.updateRecyclerAdapter(songItems);

        gotoPos(currentIndex);
    }

    @Override
    protected void updateRecyclerView() {
        super.updateRecyclerView();

        gotoPos(currentIndex);
    }

    @Override
    protected SongsRecyclerAdapter onCreateRecyclerAdapter() {
        return new SongsRecyclerAdapter(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                if(playlistPlayer != null) playlistPlayer.play(position, playCallback);
            }
        });
    }

    private PlaylistPlayer.PlayCallback playCallback = new PlaylistPlayer.PlayCallback() {
        @Override
        public void onNotReady(PlaylistPlayer playlistPlayer) {

        }

        @Override
        public void onPreparing(PlaylistPlayer playlistPlayer) {

        }

        @Override
        public void onPlaying(PlaylistPlayer playlistPlayer) {
            playlistPlayer.pause();
        }

        @Override
        public void onPaused(PlaylistPlayer playlistPlayer) {
            playlistPlayer.resume();
        }
    };

    @Override
    protected SongsModel onCreateModel() {
        playlistModel = getModel(getArguments().getInt("modelOwner"), PlaylistModel.class);
        return playlistModel;
    }

    @Override
    protected LinearLayoutManager onCreateLinearLayoutManager() {
        layoutManager = new MyLinearLayoutManager(getContext());
        return layoutManager;
    }
}
