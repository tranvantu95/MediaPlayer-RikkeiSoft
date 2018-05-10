package com.ccs.app.musicplayer.custom.adapter.base;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccs.app.musicplayer.R;
import com.ccs.app.musicplayer.model.base.BaseItem;

public abstract class MyRecyclerAdapter<Item extends BaseItem,
        VH extends MyRecyclerAdapter.ViewHolder<Item, ?>>
        extends SwitchRecyclerAdapter<Item, RecyclerView, LinearLayoutManager, GridLayoutManager, VH> {

    private int currentId;

    public int getCurrentId() {
        return currentId;
    }

    public void setCurrentId(int currentId) {
        this.currentId = currentId;
    }

    private boolean playing;

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public MyRecyclerAdapter(OnItemClickListener onItemClickListener) {
        super(onItemClickListener);
    }

    @Override
    protected int getItemListLayoutId() {
        return R.layout.item_list;
    }

    @Override
    protected int getItemGridLayoutId() {
        return R.layout.item_grid;
    }

    public static class ViewHolder<Item extends BaseItem,
            RA extends MyRecyclerAdapter<Item, ?>>
            extends SwitchRecyclerAdapter.ViewHolder<Item,
            RecyclerView, LinearLayoutManager, GridLayoutManager, RA> {

        protected TextView tvTitle;
        protected TextView tvInfo;
        protected ImageView ivCover;
        private ImageView ivStatus;

        public ViewHolder(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager,
                          LinearLayoutManager linearLayoutManager, GridLayoutManager gridLayoutManager,
                          RA recyclerAdapter, View itemView) {
            super(recyclerView, layoutManager, linearLayoutManager, gridLayoutManager, recyclerAdapter, itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvInfo = itemView.findViewById(R.id.tv_info);
            ivCover = itemView.findViewById(R.id.iv_cover);
            ivStatus = itemView.findViewById(R.id.iv_status);
        }

        @Override
        public void setItem(Item item, int position) {
            super.setItem(item, position);

            tvTitle.setText(item.getName());
            tvInfo.setText(item.getInfo());

            if(item.getId() == recyclerAdapter.getCurrentId()) {
                ivStatus.setVisibility(View.VISIBLE);
                ivStatus.setImageResource(recyclerAdapter.isPlaying() ? R.drawable.ic_play_circle : R.drawable.ic_pause_circle);
            }
            else ivStatus.setVisibility(View.GONE);
        }
    }
}
