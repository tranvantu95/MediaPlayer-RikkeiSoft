package com.rikkeisoft.musicplayer.custom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist_main, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends BaseRecyclerAdapter.ViewHolder<ArtistItem> {

        private TextView tvArtistName;

        public ViewHolder(View itemView) {
            super(itemView);

            tvArtistName = itemView.findViewById(R.id.artist_name);
        }

        @Override
        public void setItem(ArtistItem artistItem) {
            super.setItem(artistItem);

            tvArtistName.setText(artistItem.getName());
        }
    }
}
