package com.rikkeisoft.musicplayer.activity.base;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.base.BaseListModel;

import java.util.List;

public class BaseListFragment<Item, Model extends BaseListModel<Item>,
        RecyclerAdapter extends BaseRecyclerAdapter<Item, ? >> extends BaseFragment<Model> {

    protected RecyclerAdapter recyclerAdapter;

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
        model.getItems().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(@Nullable List<Item> items) {
                if(items != null) {
                    recyclerAdapter.setItems(items);
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);
        if(itemDecoration != null) recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setPadding(divider, divider, divider, divider);
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
