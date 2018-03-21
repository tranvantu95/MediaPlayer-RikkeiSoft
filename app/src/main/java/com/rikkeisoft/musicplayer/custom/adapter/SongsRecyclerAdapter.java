package com.rikkeisoft.musicplayer.custom.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.item.SongItem;

import com.rikkeisoft.musicplayer.R;

public class SongsRecyclerAdapter extends BaseRecyclerAdapter<SongItem, SongsRecyclerAdapter.ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        SongItem songItem = items.get(position);

        holder.tvSongName.setText(songItem.getName());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSongName;

        public ViewHolder(View itemView) {
            super(itemView);

            tvSongName = itemView.findViewById(R.id.song_name);
        }
    }
}
