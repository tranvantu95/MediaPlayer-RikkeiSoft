package com.rikkeisoft.musicplayer.model.base;

import android.arch.lifecycle.MutableLiveData;

import com.rikkeisoft.musicplayer.custom.adapter.base.SwitchTypeViewRecyclerAdapter;

public class SwitchTypeViewModel<Item> extends BaseListModel<Item> {

    public static final int LIST = SwitchTypeViewRecyclerAdapter.LIST_VIEW;
    public static final int GRID = SwitchTypeViewRecyclerAdapter.GRID_VIEW;

    private MutableLiveData<Integer> typeView;

    public MutableLiveData<Integer> getTypeView() {
        if(typeView == null) typeView = new MutableLiveData<>();
        return typeView;
    }

}
