package com.ccs.app.musicplayer.activity.fragment;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.ccs.app.musicplayer.activity.AlbumActivity;
import com.ccs.app.musicplayer.activity.base.MyFragment;
import com.ccs.app.musicplayer.custom.adapter.AlbumsRecyclerAdapter;
import com.ccs.app.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.ccs.app.musicplayer.model.AlbumsModel;
import com.ccs.app.musicplayer.model.PlayerModel;
import com.ccs.app.musicplayer.model.item.AlbumItem;
import com.ccs.app.musicplayer.model.item.SongItem;

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
    protected void onCurrentSongChange(@NonNull SongItem songItem) {
        recyclerAdapter.setCurrentId(songItem.getAlbumId());
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
