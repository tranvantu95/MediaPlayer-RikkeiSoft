package com.rikkeisoft.musicplayer.activity.base;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.custom.adapter.base.MyRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.SwitchRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.base.BaseItem;
import com.rikkeisoft.musicplayer.model.base.SwitchListModel;

public abstract class MyFragment<Item,
        Model extends SwitchListModel<Item>,
        RA extends SwitchRecyclerAdapter<Item, RecyclerView, LinearLayoutManager, GridLayoutManager, ?>>
        extends SwitchListFragment<Item, Model, RecyclerView, LinearLayoutManager, GridLayoutManager, RA> {

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
