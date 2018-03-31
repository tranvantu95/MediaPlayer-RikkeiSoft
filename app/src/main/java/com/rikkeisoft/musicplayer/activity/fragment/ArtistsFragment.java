package com.rikkeisoft.musicplayer.activity.fragment;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.rikkeisoft.musicplayer.activity.ArtistActivity;
import com.rikkeisoft.musicplayer.activity.base.MyFragment;
import com.rikkeisoft.musicplayer.app.MyApplication;
import com.rikkeisoft.musicplayer.custom.adapter.ArtistsRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.MyRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.SwitchRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.ArtistsModel;
import com.rikkeisoft.musicplayer.model.base.SwitchListModel;
import com.rikkeisoft.musicplayer.model.item.ArtistItem;

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

        MyApplication.getPlayerModel().getCurrentArtistId().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer != null) {
                    recyclerAdapter.setCurrentId(integer);
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        });

        MyApplication.getPlayerModel().getPlaying().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean != null) {
                    recyclerAdapter.setPlaying(aBoolean);
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        });
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
