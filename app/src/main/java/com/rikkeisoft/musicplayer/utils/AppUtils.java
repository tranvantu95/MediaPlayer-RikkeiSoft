package com.rikkeisoft.musicplayer.utils;

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.MainActivity;

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

    // Activity
    public static Intent makeMainActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        return intent;
    }

    // Service
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null)
            for(ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE))
                if(serviceClass.getName().equals(service.service.getClassName())) return true;

        return false;
    }

    // Widget
    public static AppWidgetManager getAppWidgetManager(Context context) {
        return AppWidgetManager.getInstance(context);
    }

    public static int[] getAppWidgetIds(Context context, AppWidgetManager appWidgetManager, Class<?> widgetClass) {
        return appWidgetManager.getAppWidgetIds(new ComponentName(context, widgetClass));
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
