package com.rikkeisoft.musicplayer.custom.adapter;

import android.view.View;
import android.view.ViewGroup;

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

            title.setText(artistItem.getName());

            int na = artistItem.getNumberOfAlbums();
            int ns = artistItem.getNumberOfSongs();
            String infoString = "" + na + " album" + (na > 1 ? "s" : "") + " | " + ns + " song" + (ns > 1 ? "s" : "");

            info.setText(infoString);

            if(artistItem.getBitmap() != null) image.setImageBitmap(artistItem.getBitmap());
            else image.setImageDrawable(image.getContext().getResources().getDrawable(R.drawable.im_artist));
        }
    }
}
