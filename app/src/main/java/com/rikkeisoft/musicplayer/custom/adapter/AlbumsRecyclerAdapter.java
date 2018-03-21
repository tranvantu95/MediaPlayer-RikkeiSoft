package com.rikkeisoft.musicplayer.custom.adapter;


import android.support.v7.widget.RecyclerView;
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

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        AlbumItem albumItem = items.get(position);

        holder.tvAlbumName.setText(albumItem.getName());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvAlbumName;

        public ViewHolder(View itemView) {
            super(itemView);

            tvAlbumName = itemView.findViewById(R.id.album_name);
        }
    }
}
