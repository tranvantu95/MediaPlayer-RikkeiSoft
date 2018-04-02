package com.rikkeisoft.musicplayer.activity.fragment;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.rikkeisoft.musicplayer.activity.AlbumActivity;
import com.rikkeisoft.musicplayer.activity.base.MyFragment;
import com.rikkeisoft.musicplayer.app.MyApplication;
import com.rikkeisoft.musicplayer.custom.adapter.AlbumsRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.MyRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.SwitchRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.AlbumsModel;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.base.SwitchListModel;
import com.rikkeisoft.musicplayer.model.item.AlbumItem;
import com.rikkeisoft.musicplayer.model.item.SongItem;

public class AlbumsFragment extends MyFragment<AlbumItem, AlbumsModel, AlbumsRecyclerAdapter> {

    public static AlbumsFragment newInstance(int modelOwner) {
        AlbumsFragment fragment = new AlbumsFragment();

        Bundle args = new Bundle();
        args.putInt("modelOwner", modelOwner);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void playerModelObserve(PlayerModel playerModel) {
        super.playerModelObserve(playerModel);

        playerModel.getCurrentSong().observe(this, new Observer<SongItem>() {
            @Override
            public void onChanged(@Nullable SongItem songItem) {
                if(songItem != null) {
                    recyclerAdapter.setCurrentId(songItem.getAlbumId());
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected AlbumsModel onCreateModel() {
        return getModel(getArguments().getInt("modelOwner"), AlbumsModel.class);
    }

    @Override
    protected AlbumsRecyclerAdapter onCreateRecyclerAdapter() {
        return new AlbumsRecyclerAdapter(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                AlbumItem album = recyclerAdapter.getItems().get(position);
                getActivity().startActivity(AlbumActivity.createIntent(getContext(), album.getId()));
            }
        });
    }

}
