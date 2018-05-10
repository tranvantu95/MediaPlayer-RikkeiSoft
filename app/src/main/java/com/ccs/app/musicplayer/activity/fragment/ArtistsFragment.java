package com.ccs.app.musicplayer.activity.fragment;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.ccs.app.musicplayer.activity.ArtistActivity;
import com.ccs.app.musicplayer.activity.base.MyFragment;
import com.ccs.app.musicplayer.custom.adapter.ArtistsRecyclerAdapter;
import com.ccs.app.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.ccs.app.musicplayer.model.ArtistsModel;
import com.ccs.app.musicplayer.model.PlayerModel;
import com.ccs.app.musicplayer.model.item.ArtistItem;
import com.ccs.app.musicplayer.model.item.SongItem;

public class ArtistsFragment extends MyFragment<ArtistItem, ArtistsModel, ArtistsRecyclerAdapter> {

    public static ArtistsFragment newInstance(int modelOwner) {
        ArtistsFragment fragment = new ArtistsFragment();

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
        recyclerAdapter.setCurrentId(songItem.getArtistId());
    }

    @Override
    protected ArtistsModel onCreateModel() {
        return getModel(getArguments().getInt("modelOwner"), ArtistsModel.class);
    }

    @Override
    protected ArtistsRecyclerAdapter onCreateRecyclerAdapter() {
        return new ArtistsRecyclerAdapter(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                ArtistItem artist = recyclerAdapter.getItems().get(position);
                getActivity().startActivity(ArtistActivity.createIntent(getContext(), artist.getId()));
            }
        });
    }
}
