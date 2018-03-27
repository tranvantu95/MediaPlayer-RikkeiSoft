package com.rikkeisoft.musicplayer.custom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.item.ArtistItem;

public class ArtistsRecyclerAdapter extends BaseRecyclerAdapter<ArtistItem, ArtistsRecyclerAdapter.ViewHolder> {

    public ArtistsRecyclerAdapter(OnItemClickListener onItemClickListener) {
        super(onItemClickListener);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends BaseRecyclerAdapter.ViewHolder<ArtistItem> {

        private TextView artistName;
        private TextView artistInfo;
        private ImageView artistImage;

        public ViewHolder(View itemView) {
            super(itemView);

            artistName = itemView.findViewById(R.id.artist_name);
            artistInfo = itemView.findViewById(R.id.artist_info);
            artistImage = itemView.findViewById(R.id.artist_image);
        }

        @Override
        public void setItem(ArtistItem artistItem) {
            super.setItem(artistItem);

            artistName.setText(artistItem.getName());

            int na = artistItem.getNumberOfAlbums();
            int ns = artistItem.getNumberOfSongs();
            String info = "" + na + " album" + (na > 1 ? "s" : "") + " | " + ns + " song" + (ns > 1 ? "s" : "");

            artistInfo.setText(info);

            if(artistItem.getBitmap() != null) artistImage.setImageBitmap(artistItem.getBitmap());
            else artistImage.setImageDrawable(artistImage.getContext().getResources().getDrawable(R.drawable.im_artist));
        }
    }
}
