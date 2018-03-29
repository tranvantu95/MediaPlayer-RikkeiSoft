package com.rikkeisoft.musicplayer.custom.adapter;

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
        return new ViewHolder(view);
    }

    public static class ViewHolder extends MyRecyclerAdapter.ViewHolder<AlbumItem> {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void setItem(AlbumItem albumItem) {
            super.setItem(albumItem);

            tvTitle.setText(albumItem.getName());
            tvInfo.setText(albumItem.getArtistName());

            if(albumItem.getBitmap() != null) ivCover.setImageBitmap(albumItem.getBitmap());
            else ivCover.setImageDrawable(ivCover.getContext().getResources().getDrawable(R.drawable.im_album));
        }
    }
}
