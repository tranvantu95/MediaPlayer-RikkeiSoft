package com.rikkeisoft.musicplayer.model.base;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class BaseItem {

    private String id;

    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
