package com.rikkeisoft.musicplayer.activity.base;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.util.Log;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.custom.adapter.base.ListGridRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.base.ListGridModel;

import java.util.List;

public class ListGridFragment<Item> extends BaseListFragment {

    protected ListGridModel<Item> baseListModel;

    protected ListGridRecyclerAdapter<Item, ? > recyclerAdapter;

    protected LinearLayoutManager linearLayoutManager;
    protected GridLayoutManager gridLayoutManager;

    protected int list_divider, grid_divider;

    private Observer<Integer> onTypeViewChange;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onTypeViewChange = new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer != null) setTypeView(integer);
            }
        };
    }

    protected void init() {
        baseListModel.getItems().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(@Nullable List<Item> items) {
                recyclerAdapter.setItems(items);
                recyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    private int getDivider(int typeView) {
        divider = typeView == ListGridRecyclerAdapter.LIST_VIEW ? list_divider : grid_divider;
        return divider;
    }

    private RecyclerView.LayoutManager getLayoutManager(int typeView) {
        layoutManager = typeView == ListGridRecyclerAdapter.LIST_VIEW ? linearLayoutManager : gridLayoutManager;
        return layoutManager;
    }

    private void setTypeView(int typeView) {
        if(recyclerAdapter.getTypeView() == typeView) return;
        Log.d("debug", "setTypeView " + getClass().getSimpleName());
        recyclerAdapter.setTypeView(typeView);

        getDivider(typeView);
        int rightPadding = typeView == ListGridRecyclerAdapter.LIST_VIEW ? 0 : divider;
        recyclerView.setPadding(divider, divider, rightPadding, divider);
        recyclerView.setLayoutManager(getLayoutManager(typeView));
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recycler_view);
        if(itemDecoration != null) recyclerView.addItemDecoration(itemDecoration);

        recyclerAdapter.setTypeView(0);

        baseListModel.getTypeView().observe(this, onTypeViewChange);

//        if(baseListModel.getTypeView().getValue() == null) setTypeView(ListGridModel.LIST);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        baseListModel.getTypeView().removeObserver(onTypeViewChange);
    }
}
