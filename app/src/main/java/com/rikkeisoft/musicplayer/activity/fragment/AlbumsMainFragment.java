package com.rikkeisoft.musicplayer.activity.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.base.BaseMainFragment;
import com.rikkeisoft.musicplayer.custom.adapter.AlbumsMainRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.SongsMainRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseMainRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.AlbumsMainModel;
import com.rikkeisoft.musicplayer.model.SongsMainModel;
import com.rikkeisoft.musicplayer.model.item.AlbumItem;
import com.rikkeisoft.musicplayer.model.item.SongItem;

public class AlbumsMainFragment extends BaseMainFragment<AlbumItem> {

    public static AlbumsMainFragment newInstance() {
        AlbumsMainFragment fragment = new AlbumsMainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("debug", "onCreate SongsMainFragment");

        baseMainModel = ViewModelProviders.of(getActivity()).get(AlbumsMainModel.class);

        adapter = new AlbumsMainRecyclerAdapter();
        adapter.setOnItemClickListener(new BaseMainRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {

            }
        });

        init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
