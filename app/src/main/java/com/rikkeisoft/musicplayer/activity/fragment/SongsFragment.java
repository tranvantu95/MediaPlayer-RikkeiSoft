package com.rikkeisoft.musicplayer.activity.fragment;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.rikkeisoft.musicplayer.activity.PlayerActivity;
import com.rikkeisoft.musicplayer.activity.base.AppbarActivity;
import com.rikkeisoft.musicplayer.activity.base.BaseFragment;
import com.rikkeisoft.musicplayer.activity.base.MyFragment;
import com.rikkeisoft.musicplayer.app.MyApplication;
import com.rikkeisoft.musicplayer.custom.adapter.SongsRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.MyRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.SwitchRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.base.SwitchListModel;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.model.SongsModel;
import com.rikkeisoft.musicplayer.utils.PlaylistPlayer;

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
            public void onChanged(@Nullable PlaylistPlayer _playlistPlayer) {
                playlistPlayer = _playlistPlayer;
            }
        });
    }

    @Override
    protected void playerModelObserve(PlayerModel playerModel) {
        super.playerModelObserve(playerModel);

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
                            || position >= playlistPlayer.getPlaylist().size())
                        playlistPlayer.setPlaylist(newTitle, model.getItems().getValue(), position, true);
                    else
                        playlistPlayer.play(position, playCallback);

                    getActivity().startActivity(PlayerActivity.createIntent(getContext()));
                }
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

        }

        @Override
        public void onPaused(PlaylistPlayer playlistPlayer) {
            playlistPlayer.resume();
        }
    };

}
