package com.rikkeisoft.musicplayer.app;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("debug", "onCreate " + getClass().getSimpleName());

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

}
