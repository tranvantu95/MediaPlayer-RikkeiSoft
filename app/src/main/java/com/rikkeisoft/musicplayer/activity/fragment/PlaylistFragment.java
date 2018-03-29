package com.rikkeisoft.musicplayer.activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.base.BaseListFragment;
import com.rikkeisoft.musicplayer.custom.adapter.PlaylistRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.PlaylistModel;
import com.rikkeisoft.musicplayer.model.item.SongItem;

public class PlaylistFragment extends BaseListFragment<SongItem, PlaylistModel,
        PlaylistRecyclerAdapter, LinearLayoutManager> {

    public static PlaylistFragment newInstance(int modelOwner) {
        PlaylistFragment fragment = new PlaylistFragment();

        Bundle args = new Bundle();
        args.putInt("modelOwner", modelOwner);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected PlaylistModel onCreateModel() {
        return getModel(getArguments().getInt("modelOwner"), PlaylistModel.class);
    }

    @Override
    protected PlaylistRecyclerAdapter onCreateRecyclerAdapter() {
        return new PlaylistRecyclerAdapter(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {

            }
        });
    }

    @Override
    protected LinearLayoutManager onCreateLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    @Override
    protected int onCreateDivider() {
        return getContext().getResources().getDimensionPixelSize(R.dimen.divider_list);
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.fragment_list;
    }
}
