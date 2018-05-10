package com.ccs.app.musicplayer.custom.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ccs.app.musicplayer.R;
import com.ccs.app.musicplayer.custom.adapter.base.MyRecyclerAdapter;
import com.ccs.app.musicplayer.model.item.ArtistItem;

public class ArtistsRecyclerAdapter extends MyRecyclerAdapter<ArtistItem, ArtistsRecyclerAdapter.ViewHolder> {

    public ArtistsRecyclerAdapter(OnItemClickListener onItemClickListener) {
        super(onItemClickListener);
    }

    @Override
    protected ViewHolder getViewHolder(View view) {
        return new ViewHolder(recyclerView, layoutManager, linearLayoutManager, gridLayoutManager, this, view);
    }

    public static class ViewHolder extends MyRecyclerAdapter.ViewHolder<ArtistItem, ArtistsRecyclerAdapter> {

        public ViewHolder(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager,
                          LinearLayoutManager linearLayoutManager, GridLayoutManager gridLayoutManager,
                          ArtistsRecyclerAdapter recyclerAdapter, View itemView) {
            super(recyclerView, layoutManager, linearLayoutManager, gridLayoutManager, recyclerAdapter, itemView);
        }

        @Override
        public void setItem(ArtistItem artistItem, int position) {
            super.setItem(artistItem, position);

            if(artistItem.getBitmap() != null) ivCover.setImageBitmap(artistItem.getBitmap());
            else ivCover.setImageResource(R.drawable.im_artist);
        }
    }
}
