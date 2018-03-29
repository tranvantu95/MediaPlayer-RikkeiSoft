package com.rikkeisoft.musicplayer.custom.adapter.base;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rikkeisoft.musicplayer.R;

public abstract class MyRecyclerAdapter<Item, V extends MyRecyclerAdapter.ViewHolder<Item>>
        extends SwitchRecyclerAdapter<Item, V> {

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

    public static class ViewHolder<Item> extends BaseRecyclerAdapter.ViewHolder<Item> {

        protected TextView tvTitle;
        protected TextView tvInfo;
        protected ImageView ivCover;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvInfo = itemView.findViewById(R.id.tv_info);
            ivCover = itemView.findViewById(R.id.iv_cover);
        }
    }
}
