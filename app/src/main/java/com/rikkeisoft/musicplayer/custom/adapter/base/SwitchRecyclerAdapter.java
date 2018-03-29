package com.rikkeisoft.musicplayer.custom.adapter.base;

public abstract class SwitchRecyclerAdapter<Item, V extends BaseRecyclerAdapter.ViewHolder<Item>>
        extends BaseRecyclerAdapter<Item, V> {

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
}
