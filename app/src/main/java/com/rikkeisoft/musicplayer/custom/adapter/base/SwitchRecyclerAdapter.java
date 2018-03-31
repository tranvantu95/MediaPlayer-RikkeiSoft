package com.rikkeisoft.musicplayer.custom.adapter.base;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class SwitchRecyclerAdapter<Item,
        RV extends RecyclerView,
        LLM extends LinearLayoutManager,
        GLM extends GridLayoutManager,
        VH extends SwitchRecyclerAdapter.ViewHolder<Item, ?, ?, ?, ?>>
        extends BaseRecyclerAdapter<Item, RV, RecyclerView.LayoutManager, VH> {

    public static final int LIST_VIEW = 1;
    public static final int GRID_VIEW = 2;

    private int typeView;

    public SwitchRecyclerAdapter(OnItemClickListener onItemClickListener) {
        super(onItemClickListener);
    }

    public int getTypeView() {
        return typeView;
    }

    public void setTypeView(int typeView) {
        this.typeView = typeView;
    }

    @Override
    protected int getItemLayoutId() {
        switch (getTypeView()) {
            case LIST_VIEW:
                return getItemListLayoutId();

            case GRID_VIEW:
                return getItemGridLayoutId();

            default:
                return getItemListLayoutId();
        }
    }

    protected abstract int getItemListLayoutId();

    protected abstract int getItemGridLayoutId();

    protected LLM linearLayoutManager;
    protected GLM gridLayoutManager;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        switch (getTypeView()) {
            case LIST_VIEW:
                linearLayoutManager = (LLM) layoutManager;
                break;

            case GRID_VIEW:
                gridLayoutManager = (GLM) layoutManager;
                break;

            default:
                linearLayoutManager = (LLM) layoutManager;
        }
    }

    public static class ViewHolder<Item,
            RV extends RecyclerView,
            LLM extends LinearLayoutManager,
            GLM extends GridLayoutManager,
            RA extends SwitchRecyclerAdapter<Item, ?, ?, ?, ?>>
            extends BaseRecyclerAdapter.ViewHolder<Item, RV, RecyclerView.LayoutManager, RA> {

        protected LLM linearLayoutManager;
        protected GLM gridLayoutManager;

        public ViewHolder(RV rv, RecyclerView.LayoutManager lm, LLM llm, GLM glm, RA ra, View itemView) {
            super(rv, lm, ra, itemView);

            linearLayoutManager = llm;
            gridLayoutManager = glm;
        }
    }
}
