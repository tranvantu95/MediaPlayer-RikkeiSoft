package com.rikkeisoft.musicplayer.custom.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.rikkeisoft.musicplayer.custom.adapter.base.MyRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.item.SongItem;

import com.rikkeisoft.musicplayer.R;

public class SongsRecyclerAdapter extends MyRecyclerAdapter<SongItem, SongsRecyclerAdapter.ViewHolder> {

    public SongsRecyclerAdapter(OnItemClickListener onItemClickListener) {
        super(onItemClickListener);
    }

    @Override
    protected ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }

    public static class ViewHolder extends MyRecyclerAdapter.ViewHolder<SongItem> {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void setItem(SongItem songItem) {
            super.setItem(songItem);

            title.setText(songItem.getName());
            info.setText(songItem.getArtistName());

            if(songItem.getBitmap() != null) image.setImageBitmap(songItem.getBitmap());
            else image.setImageDrawable(image.getContext().getResources().getDrawable(R.drawable.im_song));
        }
    }
}
