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

public abstract class SwitchListFragment<Item,
        Model extends SwitchListModel<Item>,
        RV extends RecyclerView,
        LLM extends LinearLayoutManager,
        GLM extends GridLayoutManager,
        RA extends SwitchRecyclerAdapter<Item, ?, ?, ?, ?>>
        extends BaseListFragment<Item, Model, RV, RecyclerView.LayoutManager, RA> {

    protected LLM linearLayoutManager;
    protected GLM gridLayoutManager;

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

    protected abstract LLM onCreateLinearLayoutManager();

    protected abstract GLM onCreateGridLayoutManager();

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
