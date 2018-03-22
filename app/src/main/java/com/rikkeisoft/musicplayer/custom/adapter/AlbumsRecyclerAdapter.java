package com.rikkeisoft.musicplayer.custom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.item.AlbumItem;

public class AlbumsRecyclerAdapter extends BaseRecyclerAdapter<AlbumItem, AlbumsRecyclerAdapter.ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_main, parent, false);
        return new ViewHolder(view);
    }

//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        super.onBindViewHolder(holder, position);
//
//    }

    public static class ViewHolder extends BaseRecyclerAdapter.ViewHolder<AlbumItem> {

        private TextView tvAlbumName;

        public ViewHolder(View itemView) {
            super(itemView);

            tvAlbumName = itemView.findViewById(R.id.album_name);
        }

        @Override
        public void setItem(AlbumItem albumItem) {
            super.setItem(albumItem);

            tvAlbumName.setText(albumItem.getName());
        }
    }
}
