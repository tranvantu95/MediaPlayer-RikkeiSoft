package com.rikkeisoft.musicplayer.activity.base;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.ListGridRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.base.BaseListModel;
import com.rikkeisoft.musicplayer.model.base.ListGridModel;

import java.util.List;

public class BaseListFragment<Item> extends Fragment {

    protected BaseListModel<Item> baseListModel;

    protected BaseRecyclerAdapter<Item, ? > recyclerAdapter;

    protected RecyclerView recyclerView;

    protected RecyclerView.ItemDecoration itemDecoration;

    protected RecyclerView.LayoutManager layoutManager;

    protected int divider;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("debug", "onCreate " + getClass().getSimpleName());

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);
        if(itemDecoration != null) recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(itemDecoration != null) recyclerView.removeItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(null);
        recyclerView.setAdapter(null);
        recyclerView = null;
    }
}
