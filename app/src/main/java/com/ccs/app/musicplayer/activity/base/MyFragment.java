package com.ccs.app.musicplayer.activity.base;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ccs.app.musicplayer.R;
import com.ccs.app.musicplayer.custom.adapter.base.MyRecyclerAdapter;
import com.ccs.app.musicplayer.model.PlayerModel;
import com.ccs.app.musicplayer.model.base.BaseItem;
import com.ccs.app.musicplayer.model.base.MyModel;
import com.ccs.app.musicplayer.model.item.SongItem;

public abstract class MyFragment<Item extends BaseItem,
        Model extends MyModel<Item>,
        RA extends MyRecyclerAdapter<Item, ?>>
        extends SwitchListFragment<Item, Model, RecyclerView, LinearLayoutManager, GridLayoutManager, RA> {

    protected PlayerModel playerModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model.getPayerModel().observe(this, new Observer<PlayerModel>() {
            @Override
            public void onChanged(@Nullable PlayerModel playerModel) {
                if(playerModel != null) onPlayerModelCreated(playerModel);
            }
        });
    }

    protected void onPlayerModelCreated(@NonNull PlayerModel playerModel) {
        Log.d("debug", "onPlayerModelCreated " + getClass().getSimpleName());
        this.playerModel = playerModel;

        playerModel.getPlaying().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean != null) {
                    recyclerAdapter.setPlaying(aBoolean);
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        });

        playerModel.getCurrentSong().observe(this, new Observer<SongItem>() {
            @Override
            public void onChanged(@Nullable SongItem songItem) {
                if(songItem == null) recyclerAdapter.setCurrentId(-1);
                else onCurrentSongChange(songItem);
                recyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    protected abstract void onCurrentSongChange(@NonNull SongItem songItem);

    @Override
    protected LinearLayoutManager onCreateLinearLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    @Override
    protected GridLayoutManager onCreateGridLayoutManager() {
        return new GridLayoutManager(getContext(), 2);
    }

    @Override
    protected int onCreateDividerList() {
        return getContext().getResources().getDimensionPixelSize(R.dimen.divider_list);
    }

    @Override
    protected int onCreateDividerGrid() {
        return getContext().getResources().getDimensionPixelSize(R.dimen.divider_grid);
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.fragment_list;
    }

}
