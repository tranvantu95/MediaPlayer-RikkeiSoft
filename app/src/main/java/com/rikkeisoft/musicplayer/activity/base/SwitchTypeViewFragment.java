package com.rikkeisoft.musicplayer.activity.base;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.rikkeisoft.musicplayer.custom.adapter.base.SwitchTypeViewRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.base.SwitchTypeViewModel;

public class SwitchTypeViewFragment<Item> extends BaseListFragment<Item, SwitchTypeViewModel<Item>,
        SwitchTypeViewRecyclerAdapter<Item, ? >> {

    protected LinearLayoutManager linearLayoutManager;
    protected GridLayoutManager gridLayoutManager;

    protected int dividerList, dividerGrid;

    @Override
    protected void init() {
        super.init();

        model.getTypeView().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer != null) setTypeView(integer);
            }
        });

        layoutManager = linearLayoutManager;
        divider = dividerList;
    }

    private int switchDivider(int typeView) {
        switch (typeView) {
            case SwitchTypeViewRecyclerAdapter.LIST_VIEW:
                return dividerList;

            case SwitchTypeViewRecyclerAdapter.GRID_VIEW:
                return dividerGrid;

            default:
                return dividerList;
        }
    }

    private RecyclerView.LayoutManager switchLayoutManager(int typeView) {
        switch (typeView) {
            case SwitchTypeViewRecyclerAdapter.LIST_VIEW:
                return linearLayoutManager;

            case SwitchTypeViewRecyclerAdapter.GRID_VIEW:
                return gridLayoutManager;

            default:
                return linearLayoutManager;
        }
    }

    private void setTypeView(int typeView) {
        if(recyclerAdapter.getTypeView() == typeView) return;
        Log.d("debug", "setTypeView " + getClass().getSimpleName());

        recyclerAdapter.setTypeView(typeView);
        layoutManager = switchLayoutManager(typeView);
        divider = switchDivider(typeView);

        if(recyclerView != null) updateRecyclerView();
    }

}
