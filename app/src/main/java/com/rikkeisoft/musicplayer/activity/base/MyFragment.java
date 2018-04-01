package com.rikkeisoft.musicplayer.activity.base;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.app.MyApplication;
import com.rikkeisoft.musicplayer.custom.adapter.base.MyRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.SwitchRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.base.BaseItem;
import com.rikkeisoft.musicplayer.model.base.MyModel;
import com.rikkeisoft.musicplayer.model.base.SwitchListModel;
import com.rikkeisoft.musicplayer.utils.PlaylistPlayer;

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
                if(playerModel != null) playerModelObserve(playerModel);
            }
        });
    }

    protected void playerModelObserve(PlayerModel playerModel) {
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
    }

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
