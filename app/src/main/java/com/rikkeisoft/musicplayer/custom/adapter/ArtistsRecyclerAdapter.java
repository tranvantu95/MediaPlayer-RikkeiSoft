package com.rikkeisoft.musicplayer.custom.adapter;

import android.view.View;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.custom.adapter.base.MyRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.item.ArtistItem;

public class ArtistsRecyclerAdapter extends MyRecyclerAdapter<ArtistItem, ArtistsRecyclerAdapter.ViewHolder> {

    public ArtistsRecyclerAdapter(OnItemClickListener onItemClickListener) {
        super(onItemClickListener);
    }

    @Override
    protected ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }

    public static class ViewHolder extends MyRecyclerAdapter.ViewHolder<ArtistItem> {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void setItem(ArtistItem artistItem) {
            super.setItem(artistItem);

            tvTitle.setText(artistItem.getName());

            int na = artistItem.getNumberOfAlbums();
            int ns = artistItem.getNumberOfSongs();
            String infoString = "" + na + " album" + (na > 1 ? "s" : "") + " | " + ns + " song" + (ns > 1 ? "s" : "");

            tvInfo.setText(infoString);

            if(artistItem.getBitmap() != null) ivCover.setImageBitmap(artistItem.getBitmap());
            else ivCover.setImageDrawable(ivCover.getContext().getResources().getDrawable(R.drawable.im_artist));
        }
    }
}
