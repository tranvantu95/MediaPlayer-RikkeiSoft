package com.rikkeisoft.musicplayer.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class AppUtils {

    // Fragment
    public static Fragment getPagerFragment(FragmentManager fm, int viewPagerId, int pagerPosition) {
        return fm.findFragmentByTag("android:switcher:" + viewPagerId + ":" + pagerPosition);
    }

}
