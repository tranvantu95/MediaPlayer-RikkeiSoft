package com.rikkeisoft.musicplayer.custom.adapter.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rikkeisoft.musicplayer.R;

public abstract class MyRecyclerAdapter<Item, V extends MyRecyclerAdapter.ViewHolder<Item>>
        extends ListGridRecyclerAdapter<Item, V> {

    public MyRecyclerAdapter(OnItemClickListener onItemClickListener) {
        super(onItemClickListener);
    }

    protected View getView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(
                getTypeView() == LIST_VIEW ? R.layout.item_list : R.layout.item_grid,
                parent, false);
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
