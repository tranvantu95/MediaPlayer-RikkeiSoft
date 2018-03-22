package com.rikkeisoft.musicplayer.app;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        android.util.Log.d("debug", "onCreate MyApplication");

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

}
