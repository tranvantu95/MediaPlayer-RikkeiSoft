package com.rikkeisoft.musicplayer.activity.fragment;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.rikkeisoft.musicplayer.activity.PlayerActivity;
import com.rikkeisoft.musicplayer.activity.base.MyFragment;
import com.rikkeisoft.musicplayer.custom.adapter.SongsRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.model.SongsModel;
import com.rikkeisoft.musicplayer.utils.PlaylistPlayer;

import java.util.ArrayList;

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
    protected void onPlayerModelCreated(@NonNull PlayerModel playerModel) {
        super.onPlayerModelCreated(playerModel);

        playerModel.getCurrentSong().observe(this, new Observer<SongItem>() {
            @Override
            public void onChanged(@Nullable SongItem songItem) {
                if(songItem != null) {
                    recyclerAdapter.setCurrentId(songItem.getId());
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        });
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
                    String newTitle = getActivity().getTitle().toString();

                    if (!newTitle.equals(playlistPlayer.getPlaylistId())
                            || playlistPlayer.getPlaylist().size() < recyclerAdapter.getItems().size())
                        playlistPlayer.setPlaylist(newTitle, new ArrayList<>(recyclerAdapter.getItems()), position, true);
                    else
                        playlistPlayer.play(position, null);

                    getActivity().startActivity(PlayerActivity.createIntent(getContext()));
                }
            }
        });
    }

}
