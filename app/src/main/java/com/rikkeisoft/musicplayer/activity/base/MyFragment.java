package com.rikkeisoft.musicplayer.activity.base;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;

import com.rikkeisoft.musicplayer.R;

public abstract class MyFragment<Item> extends SwitchListFragment<Item> {

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
