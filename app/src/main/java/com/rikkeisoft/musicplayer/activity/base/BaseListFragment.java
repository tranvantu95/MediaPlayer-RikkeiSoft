package com.rikkeisoft.musicplayer.activity.base;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.util.Log;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.base.BaseListModel;

import java.util.List;

public class BaseListFragment<Item> extends Fragment {

    protected BaseListModel<Item> baseListModel;

    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;
    protected BaseRecyclerAdapter<Item, ? > recyclerAdapter;
    protected RecyclerView.ItemDecoration itemDecoration;

//    public static BaseListFragment newInstance() {
//        BaseListFragment fragment = new BaseListFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }

    protected void init() {
        baseListModel.getItems().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(@Nullable List<Item> items) {
                recyclerAdapter.setItems(items);
                recyclerAdapter.notifyDataSetChanged();
            }
        });
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_main, container, false);
//    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        if(itemDecoration != null) recyclerView.addItemDecoration(itemDecoration);
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        recyclerView.setLayoutManager(null);
        recyclerView.setAdapter(null);
        if(itemDecoration != null) recyclerView.removeItemDecoration(itemDecoration);
        recyclerView = null;
    }
}
