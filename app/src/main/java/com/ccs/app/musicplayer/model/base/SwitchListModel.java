package com.ccs.app.musicplayer.model.base;

import android.arch.lifecycle.MutableLiveData;

import com.ccs.app.musicplayer.custom.adapter.base.SwitchRecyclerAdapter;

public class SwitchListModel<Item> extends BaseListModel<Item> {

    public static final int LIST = SwitchRecyclerAdapter.LIST_VIEW;
    public static final int GRID = SwitchRecyclerAdapter.GRID_VIEW;

    private MutableLiveData<Integer> typeView;

    public MutableLiveData<Integer> getTypeView() {
        if(typeView == null) typeView = new MutableLiveData<>();
        return typeView;
    }

}
