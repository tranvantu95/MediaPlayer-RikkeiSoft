package com.rikkeisoft.musicplayer.custom.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.item.ArtistItem;

public class ArtistsRecyclerAdapter extends BaseRecyclerAdapter<ArtistItem, ArtistsRecyclerAdapter.ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        ArtistItem artistItem = items.get(position);

        holder.tvArtistName.setText(artistItem.getName());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvArtistName;

        public ViewHolder(View itemView) {
            super(itemView);

            tvArtistName = itemView.findViewById(R.id.artist_name);
        }
    }
}
