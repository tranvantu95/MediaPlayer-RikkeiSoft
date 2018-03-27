package com.rikkeisoft.musicplayer.custom.adapter.base;

public abstract class ListGridRecyclerAdapter<Item, V extends BaseRecyclerAdapter.ViewHolder<Item>>
        extends BaseRecyclerAdapter<Item, V> {

    public static final int LIST_VIEW = 1;
    public static final int GRID_VIEW = 2;

    private int typeView;

    public ListGridRecyclerAdapter(OnItemClickListener onItemClickListener) {
        super(onItemClickListener);
    }

    public int getTypeView() {
        return typeView;
    }

    public void setTypeView(int typeView) {
        this.typeView = typeView;
    }

}
