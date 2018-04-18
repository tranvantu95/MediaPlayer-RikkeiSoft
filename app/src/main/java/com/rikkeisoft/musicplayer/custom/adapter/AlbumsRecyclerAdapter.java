package com.rikkeisoft.musicplayer.custom.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.custom.adapter.base.MyRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.item.AlbumItem;

public class AlbumsRecyclerAdapter extends MyRecyclerAdapter<AlbumItem, AlbumsRecyclerAdapter.ViewHolder> {

    public AlbumsRecyclerAdapter(OnItemClickListener onItemClickListener) {
        super(onItemClickListener);
    }

    @Override
    protected ViewHolder getViewHolder(View view) {
        return new ViewHolder(recyclerView, layoutManager, linearLayoutManager, gridLayoutManager, this, view);
    }

    public static class ViewHolder extends MyRecyclerAdapter.ViewHolder<AlbumItem, AlbumsRecyclerAdapter> {

        public ViewHolder(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager,
                          LinearLayoutManager linearLayoutManager, GridLayoutManager gridLayoutManager,
                          AlbumsRecyclerAdapter recyclerAdapter, View itemView) {
            super(recyclerView, layoutManager, linearLayoutManager, gridLayoutManager, recyclerAdapter, itemView);
        }

        @Override
        public void setItem(AlbumItem albumItem, int position) {
            super.setItem(albumItem, position);

            if(albumItem.getBitmap() != null) ivCover.setImageBitmap(albumItem.getBitmap());
            else ivCover.setImageResource(R.drawable.im_album);
        }
    }
}
