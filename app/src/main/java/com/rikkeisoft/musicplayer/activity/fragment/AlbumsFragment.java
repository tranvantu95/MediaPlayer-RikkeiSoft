package com.rikkeisoft.musicplayer.activity.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.AlbumActivity;
import com.rikkeisoft.musicplayer.activity.base.ListGridFragment;
import com.rikkeisoft.musicplayer.activity.base.MyFragment;
import com.rikkeisoft.musicplayer.custom.adapter.AlbumsRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.AlbumsModel;
import com.rikkeisoft.musicplayer.model.item.AlbumItem;

public class AlbumsFragment extends MyFragment<AlbumItem> {

    public static AlbumsFragment newInstance() {
        AlbumsFragment fragment = new AlbumsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        baseListModel = ViewModelProviders.of(getActivity()).get(AlbumsModel.class);

        recyclerAdapter = new AlbumsRecyclerAdapter(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                AlbumItem album = recyclerAdapter.getItems().get(position);
                getActivity().startActivity(AlbumActivity.createIntent(getContext(), album.getId()));
            }
        });

        init();
    }

}
