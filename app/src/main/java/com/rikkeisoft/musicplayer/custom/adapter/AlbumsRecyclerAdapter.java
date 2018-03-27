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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends BaseRecyclerAdapter.ViewHolder<AlbumItem> {

        private TextView albumName;
        private TextView artistName;
        private ImageView albumImage;

        public ViewHolder(View itemView) {
            super(itemView);

            albumName = itemView.findViewById(R.id.album_name);
            artistName = itemView.findViewById(R.id.artist_name);
            albumImage = itemView.findViewById(R.id.album_image);
        }

        @Override
        public void setItem(AlbumItem albumItem) {
            super.setItem(albumItem);

            albumName.setText(albumItem.getName());
            artistName.setText(albumItem.getArtistName());

            if(albumItem.getBitmap() != null) albumImage.setImageBitmap(albumItem.getBitmap());
            else albumImage.setImageDrawable(albumImage.getContext().getResources().getDrawable(R.drawable.im_album));
        }
    }
}
