package com.ccs.app.musicplayer.activity.fragment;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.ccs.app.musicplayer.activity.PlayerActivity;
import com.ccs.app.musicplayer.activity.base.AppbarActivity;
import com.ccs.app.musicplayer.activity.base.MyFragment;
import com.ccs.app.musicplayer.custom.adapter.SongsRecyclerAdapter;
import com.ccs.app.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.ccs.app.musicplayer.model.item.SongItem;
import com.ccs.app.musicplayer.model.SongsModel;
import com.ccs.app.musicplayer.utils.ArrayUtils;
import com.ccs.app.musicplayer.player.PlaylistPlayer;

import java.util.ArrayList;
import java.util.List;

public class SongsFragment extends MyFragment<SongItem, SongsModel, SongsRecyclerAdapter> {

    public static SongsFragment newInstance(int modelOwner) {
        SongsFragment fragment = new SongsFragment();

        Bundle args = new Bundle();
        args.putInt("modelOwner", modelOwner);
        fragment.setArguments(args);

        return fragment;
    }

    protected PlaylistPlayer playlistPlayer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model.getPlaylistPlayer().observe(this, new Observer<PlaylistPlayer>() {
            @Override
            public void onChanged(@Nullable PlaylistPlayer playlistPlayer) {
                onPlaylistPlayerCreated(playlistPlayer);
            }
        });
    }

    @Override
    protected void onCurrentSongChange(@NonNull SongItem songItem) {
        recyclerAdapter.setCurrentId(songItem.getId());
    }

    protected void onPlaylistPlayerCreated(@Nullable PlaylistPlayer playlistPlayer) {
        Log.d("debug", "onPlaylistPlayerCreated " + getClass().getSimpleName());
        this.playlistPlayer = playlistPlayer;
    }

    @Override
    protected SongsModel onCreateModel() {
        return getModel(getArguments().getInt("modelOwner"), SongsModel.class);
    }

    @Override
    protected SongsRecyclerAdapter onCreateRecyclerAdapter() {
        return new SongsRecyclerAdapter(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                if(playlistPlayer != null) {
                    String oldPlaylistName = playlistPlayer.getPlaylistName();
                    String newPlaylistName = getActivity() instanceof AppbarActivity
                            ? ((AppbarActivity) getActivity()).getTitle2() : "";
                    List<SongItem> oldPlaylist = playlistPlayer.getPlaylist();
                    List<SongItem> newPlaylist = recyclerAdapter.getItems();

                    if (!newPlaylistName.equals(oldPlaylistName) || !ArrayUtils.equalsList(oldPlaylist, newPlaylist))
                        playlistPlayer.setPlaylist(newPlaylistName, new ArrayList<>(newPlaylist), position, true);
                    else
                        playlistPlayer.play(position, playCallback);

                    getActivity().startActivity(PlayerActivity.createIntent(getContext()));
                }
            }
        });
    }

    private PlaylistPlayer.PlayCallback playCallback = new PlaylistPlayer.PlayCallback() {
        @Override
        public void onNotReady(PlaylistPlayer playlistPlayer) {}

        @Override
        public void onPreparing(PlaylistPlayer playlistPlayer) {}

        @Override
        public void onPlaying(PlaylistPlayer playlistPlayer) {}

        @Override
        public void onPaused(PlaylistPlayer playlistPlayer) {
            playlistPlayer.start();
        }

        @Override
        public void isNewIndex(PlaylistPlayer playlistPlayer) {
            playlistPlayer.updateCurrentShuffleIndex();
        }
    };
}
