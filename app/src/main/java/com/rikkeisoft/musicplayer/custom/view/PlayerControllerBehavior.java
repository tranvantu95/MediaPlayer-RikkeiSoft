package com.rikkeisoft.musicplayer.custom.view;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;


public class PlayerControllerBehavior extends CoordinatorLayout.Behavior {

    private Callback callback;

    public PlayerControllerBehavior(Callback callback) {
        this.callback = callback;
    }

    public PlayerControllerBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
//        int offset = -dependency.getTop() * child.getHeight() / dependency.findViewById(R.id.toolbar).getHeight();
//        android.util.Log.d("debug", "offset " + offset);
        if(callback != null) child.setTranslationY(callback.calculate(parent, child, dependency));
        return false;
    }

    public interface Callback {
        int calculate(CoordinatorLayout parent, View child, View dependency);
    }
}
