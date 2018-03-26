package com.rikkeisoft.musicplayer.custom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.item.AlbumItem;

public class AlbumsRecyclerAdapter extends BaseRecyclerAdapter<AlbumItem, AlbumsRecyclerAdapter.ViewHolder> {

    public AlbumsRecyclerAdapter(OnItemClickListener onItemClickListener) {
        super(onItemClickListener);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_main, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends BaseRecyclerAdapter.ViewHolder<AlbumItem> {

        private ImageView imAlbumArt;
        private TextView tvAlbumName;
        private TextView tvArtistName;

        public ViewHolder(View itemView) {
            super(itemView);

            imAlbumArt = itemView.findViewById(R.id.album_art);
            tvAlbumName = itemView.findViewById(R.id.album_name);
            tvArtistName = itemView.findViewById(R.id.artist_name);
        }

        @Override
        public void setItem(AlbumItem albumItem) {
            super.setItem(albumItem);

            imAlbumArt.setImageBitmap(albumItem.getAlbumArtBitmap());

            tvAlbumName.setText(albumItem.getName());
            tvArtistName.setText(albumItem.getArtistName());
        }
    }
}
