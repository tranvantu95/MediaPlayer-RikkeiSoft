package com.rikkeisoft.musicplayer.activity.base;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.rikkeisoft.musicplayer.custom.adapter.base.SwitchRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.base.SwitchListModel;

public abstract class SwitchListFragment<Item> extends BaseListFragment<Item, SwitchListModel<Item>,
        SwitchRecyclerAdapter<Item, ? >, RecyclerView.LayoutManager> {

    protected LinearLayoutManager linearLayoutManager;
    protected GridLayoutManager gridLayoutManager;

    protected int dividerList, dividerGrid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model.getTypeView().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer != null) setTypeView(integer);
            }
        });
    }

    @Override
    protected RecyclerView.LayoutManager onCreateLayoutManager() {
        linearLayoutManager = onCreateLinearLayoutManager();
        gridLayoutManager = onCreateGridLayoutManager();
        return linearLayoutManager;
    }

    @Override
    protected int onCreateDivider() {
        dividerList = onCreateDividerList();
        dividerGrid = onCreateDividerGrid();
        return dividerList;
    }

    protected abstract LinearLayoutManager onCreateLinearLayoutManager();

    protected abstract GridLayoutManager onCreateGridLayoutManager();

    protected abstract int onCreateDividerList();

    protected abstract int onCreateDividerGrid();

    //
    private void setTypeView(int typeView) {
        if(recyclerAdapter.getTypeView() == typeView) return;
        Log.d("debug", "setTypeView " + getClass().getSimpleName());

        recyclerAdapter.setTypeView(typeView);
        layoutManager = switchLayoutManager(typeView);
        divider = switchDivider(typeView);

        if(recyclerView != null) updateRecyclerView();
    }

    private RecyclerView.LayoutManager switchLayoutManager(int typeView) {
        switch (typeView) {
            case SwitchRecyclerAdapter.LIST_VIEW:
                return linearLayoutManager;

            case SwitchRecyclerAdapter.GRID_VIEW:
                return gridLayoutManager;

            default:
                return linearLayoutManager;
        }
    }

    private int switchDivider(int typeView) {
        switch (typeView) {
            case SwitchRecyclerAdapter.LIST_VIEW:
                return dividerList;

            case SwitchRecyclerAdapter.GRID_VIEW:
                return dividerGrid;

            default:
                return dividerList;
        }
    }

}
