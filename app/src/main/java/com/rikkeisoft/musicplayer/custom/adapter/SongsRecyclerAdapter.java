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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getView(parent));
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

//    public static class ViewHolder extends BaseRecyclerAdapter.ViewHolder<SongItem> {
//
//        private TextView songName;
//        private TextView artistName;
//        private ImageView songImage;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//
//            songName = itemView.findViewById(R.id.song_name);
//            artistName = itemView.findViewById(R.id.artist_name);
//            songImage = itemView.findViewById(R.id.song_image);
//        }
//
//        @Override
//        public void setItem(SongItem songItem) {
//            super.setItem(songItem);
//
//            songName.setText(songItem.getName());
//            artistName.setText(songItem.getArtistName());
//
//            if(songItem.getBitmap() != null) songImage.setImageBitmap(songItem.getBitmap());
//            else songImage.setImageDrawable(songImage.getContext().getResources().getDrawable(R.drawable.im_song));
//        }
//    }
}
