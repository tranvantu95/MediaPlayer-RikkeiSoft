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

        protected TextView title;
        protected TextView info;
        protected ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            info = itemView.findViewById(R.id.info);
            image = itemView.findViewById(R.id.image);
        }
    }
}
