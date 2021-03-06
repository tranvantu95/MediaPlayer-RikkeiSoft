package com.ccs.app.musicplayer.custom.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ccs.app.musicplayer.custom.adapter.base.MyRecyclerAdapter;
import com.ccs.app.musicplayer.model.item.SongItem;

import com.ccs.app.musicplayer.R;

public class SongsRecyclerAdapter extends MyRecyclerAdapter<SongItem, SongsRecyclerAdapter.ViewHolder> {

    public SongsRecyclerAdapter(OnItemClickListener onItemClickListener) {
        super(onItemClickListener);
    }

    @Override
    protected ViewHolder getViewHolder(View view) {
        return new ViewHolder(recyclerView, layoutManager, linearLayoutManager, gridLayoutManager, this, view);
    }

    public static class ViewHolder extends MyRecyclerAdapter.ViewHolder<SongItem, SongsRecyclerAdapter> {

        public ViewHolder(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager,
                          LinearLayoutManager linearLayoutManager, GridLayoutManager gridLayoutManager,
                          SongsRecyclerAdapter recyclerAdapter, View itemView) {
            super(recyclerView, layoutManager, linearLayoutManager, gridLayoutManager, recyclerAdapter, itemView);
        }

        @Override
        public void setItem(SongItem songItem, int position) {
            super.setItem(songItem, position);

            if(songItem.getBitmap() != null) ivCover.setImageBitmap(songItem.getBitmap());
            else ivCover.setImageResource(R.drawable.im_song);
        }
    }
}
