package com.rikkeisoft.musicplayer.activity.base;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.base.BaseListModel;

import java.util.List;

public abstract class BaseListFragment<Item, Model extends BaseListModel<Item>,
        RecyclerAdapter extends BaseRecyclerAdapter<Item, ? >,
        LayoutManager extends RecyclerView.LayoutManager> extends BaseFragment<Model> {

    protected RecyclerView recyclerView;

    protected RecyclerView.ItemDecoration itemDecoration;

    protected LayoutManager layoutManager;

    protected RecyclerAdapter recyclerAdapter;

    protected int divider;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recyclerAdapter = onCreateRecyclerAdapter();
        layoutManager = onCreateLayoutManager();
        divider = onCreateDivider();

        model.getItems().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(@Nullable List<Item> items) {
                if (items != null) updateRecyclerAdapter(items);
            }
        });
    }

    protected abstract RecyclerAdapter onCreateRecyclerAdapter();

    protected abstract LayoutManager onCreateLayoutManager();

    protected abstract int onCreateDivider();

    //
    protected void updateRecyclerAdapter(List<Item> items) {
        recyclerAdapter.setItems(items);
        recyclerAdapter.notifyDataSetChanged();
    }

    protected void updateRecyclerView() {
        recyclerView.setPadding(divider, divider, divider, divider);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);
        if(itemDecoration != null) recyclerView.addItemDecoration(itemDecoration);

        updateRecyclerView();
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
