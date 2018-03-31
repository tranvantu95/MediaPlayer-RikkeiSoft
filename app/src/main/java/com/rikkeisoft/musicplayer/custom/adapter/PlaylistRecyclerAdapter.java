/*
package com.rikkeisoft.musicplayer.custom.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.MyRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.item.SongItem;

public class PlaylistRecyclerAdapter extends BaseRecyclerAdapter<SongItem, RecyclerView,
        LinearLayoutManager,
        SongsRecyclerAdapter.ViewHolder<PlaylistRecyclerAdapter>> {

    public PlaylistRecyclerAdapter(OnItemClickListener onItemClickListener) {
        super(onItemClickListener);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_list;
    }

    @Override
    protected SongsRecyclerAdapter.ViewHolder getViewHolder(View view) {
        return new SongsRecyclerAdapter.ViewHolder(recyclerView, this, view);
    }

    public static class ViewHolder extends MyRecyclerAdapter.ViewHolder<SongItem, RecyclerView, PlaylistRecyclerAdapter> {

        public ViewHolder(RecyclerView recyclerView, PlaylistRecyclerAdapter recyclerAdapter, View itemView) {
            super(recyclerView, recyclerAdapter, itemView);
        }

        @Override
        public void setItem(SongItem songItem) {
            super.setItem(songItem);

            tvTitle.setText(songItem.getName());
            tvInfo.setText(songItem.getArtistName());

            if(songItem.getBitmap() != null) ivCover.setImageBitmap(songItem.getBitmap());
            else ivCover.setImageDrawable(ivCover.getContext().getResources().getDrawable(R.drawable.im_song));
        }
    }
}
*/
