package com.rikkeisoft.musicplayer.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.DrawableCompat;

import com.rikkeisoft.musicplayer.R;

public class AppUtils {

    // resources
    public static Bitmap getBitmapFromVectorDrawable(Resources resources, int resId) {
        return getBitmapFromDrawable(getVectorDrawable(resources, resId));
    }

    public static Drawable getVectorDrawable(Resources resources, int resId) {
        if(currentVersionSupportVectorDrawable()) return resources.getDrawable(resId, null);
        return VectorDrawableCompat.create(resources, resId, null);
    }

    public static Bitmap getBitmapFromDrawable(Resources resources, int resId) {
        return getBitmapFromDrawable(resources.getDrawable(resId));
    }

    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        if(drawable == null) return null;

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    // Fragment
    public static Fragment getPagerFragment(FragmentManager fm, int viewPagerId, int pagerPosition) {
        return fm.findFragmentByTag("android:switcher:" + viewPagerId + ":" + pagerPosition);
    }

    // Service
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if(manager != null)
            for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
                if(serviceClass.getName().equals(service.service.getClassName())) return true;

        return false;
    }

    // check support
    public static boolean currentVersionSupportLockScreenControls() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean currentVersionSupportBigNotification() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean currentVersionSupportVectorDrawable() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

}
