package com.rikkeisoft.musicplayer.custom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.item.SongItem;

import com.rikkeisoft.musicplayer.R;

public class SongsRecyclerAdapter extends BaseRecyclerAdapter<SongItem, SongsRecyclerAdapter.ViewHolder> {

    public SongsRecyclerAdapter(OnItemClickListener onItemClickListener) {
        super(onItemClickListener);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends BaseRecyclerAdapter.ViewHolder<SongItem> {

        private TextView songName;
        private TextView artistName;
        private ImageView songImage;

        public ViewHolder(View itemView) {
            super(itemView);

            songName = itemView.findViewById(R.id.song_name);
            artistName = itemView.findViewById(R.id.artist_name);
            songImage = itemView.findViewById(R.id.song_image);
        }

        @Override
        public void setItem(SongItem songItem) {
            super.setItem(songItem);

            songName.setText(songItem.getName());
            artistName.setText(songItem.getArtistName());

            if(songItem.getBitmap() != null) songImage.setImageBitmap(songItem.getBitmap());
            else songImage.setImageDrawable(songImage.getContext().getResources().getDrawable(R.drawable.im_song));
        }
    }
}
