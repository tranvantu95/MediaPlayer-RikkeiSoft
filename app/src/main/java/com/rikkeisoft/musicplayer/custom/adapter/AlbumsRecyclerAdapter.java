package com.rikkeisoft.musicplayer.custom.adapter;

import android.view.View;
import android.view.ViewGroup;

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

            title.setText(albumItem.getName());
            info.setText(albumItem.getArtistName());

            if(albumItem.getBitmap() != null) image.setImageBitmap(albumItem.getBitmap());
            else image.setImageDrawable(image.getContext().getResources().getDrawable(R.drawable.im_album));
        }
    }
}
