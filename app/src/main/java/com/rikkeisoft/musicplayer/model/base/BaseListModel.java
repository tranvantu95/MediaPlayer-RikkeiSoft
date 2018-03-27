package com.rikkeisoft.musicplayer.model.base;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

public class BaseListModel<Item> extends ViewModel {
    private MutableLiveData<List<Item>> items;

    public MutableLiveData<List<Item>> getItems() {
        if(items == null) items = new MutableLiveData<>();
        return items;
    }
}
