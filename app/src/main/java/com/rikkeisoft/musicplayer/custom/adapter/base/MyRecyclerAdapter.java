package com.rikkeisoft.musicplayer.custom.adapter.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rikkeisoft.musicplayer.R;

public abstract class MyRecyclerAdapter<Item, V extends MyRecyclerAdapter.ViewHolder<Item>>
        extends SwitchTypeViewRecyclerAdapter<Item, V> {

    public MyRecyclerAdapter(OnItemClickListener onItemClickListener) {
        super(onItemClickListener);
    }

    private int getLayoutId() {
        switch (getTypeView()) {
            case LIST_VIEW:
                return R.layout.item_list;

            case GRID_VIEW:
                return R.layout.item_grid;

            default:
                return R.layout.item_list;
        }
    }

    protected View getView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(getLayoutId(), parent, false);
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
