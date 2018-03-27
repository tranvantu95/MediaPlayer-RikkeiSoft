package com.rikkeisoft.musicplayer.model.base;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseItem {

    private int id;

    private String name;

    protected Bitmap bitmap;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract Bitmap getBitmap();

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
