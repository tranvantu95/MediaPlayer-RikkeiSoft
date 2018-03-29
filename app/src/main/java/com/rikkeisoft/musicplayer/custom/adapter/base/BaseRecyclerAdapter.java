package com.rikkeisoft.musicplayer.custom.adapter.base;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rikkeisoft.musicplayer.model.base.BaseListModel;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerAdapter<Item, V extends BaseRecyclerAdapter.ViewHolder<Item>>
        extends RecyclerView.Adapter<V> implements View.OnClickListener {

    private List<Item> items = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    public BaseRecyclerAdapter(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutId(), parent, false);
        return getViewHolder(view);
    }

    protected abstract int getItemLayoutId();

    protected abstract V getViewHolder(View view);

    @Override
    public void onBindViewHolder(V holder, int position) {
        Item item = items.get(position);
        holder.setItem(item);

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

    public static class ViewHolder<Item> extends RecyclerView.ViewHolder {

        protected Item item;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void setItem(Item item) {
            this.item = item;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
}
