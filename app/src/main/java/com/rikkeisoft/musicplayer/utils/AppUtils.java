package com.rikkeisoft.musicplayer.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class AppUtils {

    // Fragment
    public static Fragment getPagerFragment(FragmentManager fm, int viewPagerId, int pagerPosition) {
        return fm.findFragmentByTag("android:switcher:" + viewPagerId + ":" + pagerPosition);
    }

    // Service
    public static boolean isServiceRunning(Context context, Class _class) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if(manager != null)
            for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
                if(_class.getName().equals(service.service.getClassName())) return true;

        return false;
    }

    //
    public static boolean currentVersionSupportBigNotification() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean currentVersionSupportLockScreenControls() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }
}
