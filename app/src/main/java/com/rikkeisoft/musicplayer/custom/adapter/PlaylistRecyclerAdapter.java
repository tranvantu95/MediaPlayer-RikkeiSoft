package com.rikkeisoft.musicplayer.custom.adapter;

import android.view.View;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.item.SongItem;

public class PlaylistRecyclerAdapter extends BaseRecyclerAdapter<SongItem, SongsRecyclerAdapter.ViewHolder> {

    public PlaylistRecyclerAdapter(OnItemClickListener onItemClickListener) {
        super(onItemClickListener);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_list;
    }

    @Override
    protected SongsRecyclerAdapter.ViewHolder getViewHolder(View view) {
        return new SongsRecyclerAdapter.ViewHolder(view);
    }

}
