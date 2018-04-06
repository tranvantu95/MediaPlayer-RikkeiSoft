package com.rikkeisoft.musicplayer.custom.adapter.base;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerAdapter<Item,
        RV extends RecyclerView,
        LM extends RecyclerView.LayoutManager,
        VH extends BaseRecyclerAdapter.ViewHolder<Item, ?, ?, ?>>
        extends RecyclerView.Adapter<VH>
        implements View.OnClickListener {

    protected RV recyclerView;

    protected LM layoutManager;

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
    public void onAttachedToRecyclerView(android.support.v7.widget.RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        this.recyclerView = (RV) recyclerView;
        this.layoutManager = (LM) recyclerView.getLayoutManager();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutId(), parent, false);
        return getViewHolder(view);
    }

    protected abstract int getItemLayoutId();

    protected abstract VH getViewHolder(View view);

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Item item = items.get(position);
        holder.setItem(item, position);

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

    public static class ViewHolder<Item,
            RV extends RecyclerView,
            LM extends RecyclerView.LayoutManager,
            RA extends BaseRecyclerAdapter<Item, ?, ?, ?>>
            extends RecyclerView.ViewHolder {

        protected RV recyclerView;

        protected LM layoutManager;

        protected RA recyclerAdapter;

        protected Item item;

        protected int position;

        public ViewHolder(RV rv, LM lm, RA ra, View itemView) {
            super(itemView);

            this.recyclerView = rv;
            this.layoutManager = lm;
            this.recyclerAdapter = ra;
        }

        public void setItem(Item item, int position) {
            this.item = item;
            this.position = position;
        }

    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
}
