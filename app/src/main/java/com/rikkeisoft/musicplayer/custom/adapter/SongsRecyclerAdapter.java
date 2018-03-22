package com.rikkeisoft.musicplayer.custom.adapter;

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

//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        super.onBindViewHolder(holder, position);
//
//    }

    public static class ViewHolder extends BaseRecyclerAdapter.ViewHolder<SongItem> {

        private TextView tvSongName;

        public ViewHolder(View itemView) {
            super(itemView);

            tvSongName = itemView.findViewById(R.id.song_name);
        }

        @Override
        public void setItem(SongItem songItem) {
            super.setItem(songItem);

            tvSongName.setText(songItem.getName());
        }
    }
}
