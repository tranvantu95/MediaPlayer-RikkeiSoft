package com.rikkeisoft.musicplayer.custom.adapter.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerAdapter<T, V extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<V> implements View.OnClickListener {

    protected List<T> items = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(V holder, int position) {
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onClick(View view) {
//        int id = view.getId();
        int position = (int) view.getTag();
        onItemClickListener.onItemClick(view, position);
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
}
