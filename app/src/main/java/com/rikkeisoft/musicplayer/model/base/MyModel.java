package com.rikkeisoft.musicplayer.model.base;

import android.arch.lifecycle.MutableLiveData;

import com.rikkeisoft.musicplayer.model.PlayerModel;

public class MyModel<Item> extends SwitchListModel<Item> {

    private MutableLiveData<PlayerModel> payerModel;

    public MutableLiveData<PlayerModel> getPayerModel() {
        if(payerModel == null) payerModel = new MutableLiveData<>();

        return payerModel;
    }

}
