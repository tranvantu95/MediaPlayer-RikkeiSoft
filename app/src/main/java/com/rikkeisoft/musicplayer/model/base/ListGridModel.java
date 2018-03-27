package com.rikkeisoft.musicplayer.model.base;

import android.arch.lifecycle.MutableLiveData;

import com.rikkeisoft.musicplayer.custom.adapter.base.ListGridRecyclerAdapter;

public class ListGridModel<Item> extends BaseListModel<Item> {

    public static final int LIST = ListGridRecyclerAdapter.LIST_VIEW;
    public static final int GRID = ListGridRecyclerAdapter.GRID_VIEW;

    private MutableLiveData<Integer> typeView;

    public MutableLiveData<Integer> getTypeView() {
        if(typeView == null) typeView = new MutableLiveData<>();
        return typeView;
    }

}
