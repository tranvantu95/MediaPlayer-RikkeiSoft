package com.rikkeisoft.musicplayer.player;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.ActivityHandler;
import com.rikkeisoft.musicplayer.app.MyApplication;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.service.PlayerService;
import com.rikkeisoft.musicplayer.utils.AppUtils;

public class WidgetPlayer extends AppWidgetProvider {

    public static final String ACTION_UPDATE = "com.rikkeisoft.musicplayer.action.UPDATE_WIDGET_PLAYER";

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        Log.d("debug", "onAppWidgetOptionsChanged " + getClass().getSimpleName());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d("debug", "onReceive " + getClass().getSimpleName());

        if(ACTION_UPDATE.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppUtils.getAppWidgetManager(context);
            int[] appWidgetIds = AppUtils.getAppWidgetIds(context, appWidgetManager, getClass());
            if(appWidgetIds.length > 0) update(context, appWidgetManager, appWidgetIds);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d("debug", "onEnabled " + getClass().getSimpleName());

        SharedPreferences.Editor editor =
                context.getSharedPreferences(MyApplication.DATA, Context.MODE_PRIVATE).edit();
        editor.putBoolean("widgetEnabled", true);
        editor.apply();
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d("debug", "onDisabled " + getClass().getSimpleName());

        SharedPreferences.Editor editor =
                context.getSharedPreferences(MyApplication.DATA, Context.MODE_PRIVATE).edit();
        editor.putBoolean("widgetEnabled", false);
        editor.apply();
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d("debug", "onDeleted " + getClass().getSimpleName());
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
        Log.d("debug", "onRestored " + getClass().getSimpleName());
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("debug", "onUpdate " + getClass().getSimpleName());

        update(context, appWidgetManager, appWidgetIds);
    }

    private void update(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        PlayerService.LocalBinder binder = (PlayerService.LocalBinder)
                peekService(context, new Intent(context, PlayerService.class));
        PlayerService playerService = binder != null ? binder.getService() : null;
        PlaylistPlayer playlistPlayer = playerService != null ? playerService.getPlaylistPlayer() : null;

        //
        if(playlistPlayer != null) update(context, appWidgetManager, appWidgetIds, playlistPlayer);
        else {
            Intent intent = new Intent(context, PlayerService.class);
            intent.setAction(ACTION_UPDATE);
            context.startService(intent);
        }
    }

    private static void update(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds,
                        @Nullable PlaylistPlayer playlistPlayer) {
        Log.d("debug", "update " + WidgetPlayer.class.getSimpleName());

        SongItem currentSong = playlistPlayer != null ? playlistPlayer.getCurrentSong() : null;

        //
        Resources resources = context.getResources();

        String title = "Chạm để mở nhạc", info = null;
        Bitmap bmSong = null, bmPlay, bmNext, bmPrevious;

        if(currentSong != null) {
            title = currentSong.getName();
            info = currentSong.getArtistName();
            bmSong = currentSong.cloneBitmap();
        }

        if(bmSong == null) {
            if (!AppUtils.currentVersionSupportVectorDrawable()) {
                LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                        new ColorDrawable(resources.getColor(R.color.colorGreyLight)),
                        AppUtils.getVectorDrawable(resources, R.drawable.ic_song)});

                bmSong = AppUtils.getBitmapFromDrawable(layerDrawable);
            }
            else bmSong = AppUtils.getBitmapFromDrawable(resources, R.drawable.im_song);
        }

        bmPlay = playlistPlayer != null && playlistPlayer.isRunning()
                ? AppUtils.getBitmapFromVectorDrawable(resources, R.drawable.ic_pause)
                : AppUtils.getBitmapFromVectorDrawable(resources, R.drawable.ic_play);

        bmPrevious = AppUtils.getBitmapFromVectorDrawable(resources, R.drawable.ic_skip_previous);
        bmNext = AppUtils.getBitmapFromVectorDrawable(resources, R.drawable.ic_skip_next);

        //
        for (int widgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_player);

            //
            remoteViews.setTextViewText(R.id.tv_title, title);
            remoteViews.setTextViewText(R.id.tv_info, info);
            remoteViews.setImageViewBitmap(R.id.iv_cover, bmSong);

            remoteViews.setImageViewBitmap(R.id.btn_play, bmPlay);

            remoteViews.setImageViewBitmap(R.id.btn_previous, bmPrevious);
            remoteViews.setImageViewBitmap(R.id.btn_next, bmNext);

            //
//            Intent intent = ActivityHandler.createIntent(context, ActivityHandler.FLAG_OPEN_PLAYER);
//            PendingIntent pRoot = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Intent intent = new Intent(context, PlayerService.class);
            intent.setAction(ActivityHandler.ACTION_SHOW_PLAYER_ACTIVITY);
            PendingIntent pRoot = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.root, pRoot);

            PlayerService.setListeners(context, remoteViews);

            //
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    public static void update(Context context, @Nullable PlaylistPlayer playlistPlayer) {
        AppWidgetManager appWidgetManager = AppUtils.getAppWidgetManager(context);
        int[] appWidgetIds = AppUtils.getAppWidgetIds(context, appWidgetManager, WidgetPlayer.class);
        if(appWidgetIds.length > 0) update(context, appWidgetManager, appWidgetIds, playlistPlayer);
    }

}
