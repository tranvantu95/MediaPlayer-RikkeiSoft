package com.rikkeisoft.musicplayer.player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.MainActivity;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.utils.AppUtils;

public class PlayerNotification {

    private static boolean currentVersionSupportLockScreenControls = AppUtils.currentVersionSupportLockScreenControls();
    private static boolean currentVersionSupportBigNotification = AppUtils.currentVersionSupportBigNotification();
    private static boolean currentVersionSupportVectorDrawable = AppUtils.currentVersionSupportVectorDrawable();

    private Notification notification;

    private Bitmap bmPlay, bmPause, bmSong;

    public PlayerNotification(Context context) {

        String chanelId = "com.rikkeisoft.musicplayer.notification.player";
        String chanelName = "Beauty Music";
        String channelDescription = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if(notificationManager != null) {
                NotificationChannel channel = new NotificationChannel(chanelId, chanelName, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(channelDescription);
                notificationManager.createNotificationChannel(channel);
            }
        }

        // The PendingIntent to launch our activity if the user selects this notification
        Intent intent = MainActivity.createIntent(context, MainActivity.OPEN_PLAYER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pMain = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        intent = new Intent(context, PlayerReceiver.class);
        intent.setAction(PlayerReceiver.ACTION_DELETE_NOTIFICATION);
        PendingIntent pDelete = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, chanelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("")
                .setContentText("")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pMain)
                .setDeleteIntent(pDelete)
                .setWhen(0)
                .setCustomContentView(new RemoteViews(
                        context.getApplicationContext().getPackageName(), R.layout.notification_player_small));

        if(currentVersionSupportBigNotification)
            mBuilder.setCustomBigContentView(new RemoteViews(
                    context.getApplicationContext().getPackageName(), R.layout.notification_player_big));

        notification = mBuilder.build();

        setListeners(context);

        //
        Resources resources = context.getResources();

        setImageViewBitmap(R.id.btn_previous, AppUtils.getBitmapFromVectorDrawable(resources, R.drawable.ic_skip_previous));
        setImageViewBitmap(R.id.btn_next, AppUtils.getBitmapFromVectorDrawable(resources, R.drawable.ic_skip_next));

        if (!currentVersionSupportVectorDrawable) {
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                    new ColorDrawable(resources.getColor(R.color.colorGreyLight)),
                    AppUtils.getVectorDrawable(resources, R.drawable.ic_song)});

            bmSong = AppUtils.getBitmapFromDrawable(layerDrawable);
        }
        else bmSong = AppUtils.getBitmapFromDrawable(resources, R.drawable.im_song);

        bmPlay = AppUtils.getBitmapFromVectorDrawable(resources, R.drawable.ic_play);
        bmPause = AppUtils.getBitmapFromVectorDrawable(resources, R.drawable.ic_pause);

        //
        setSong(null);
        setPlay(false);
    }

    public Notification getNotification() {
        return notification;
    }

    public void setTitle(String text) {
        setTextViewText(R.id.tv_title, text);
    }

    public void setInfo(String text) {
        setTextViewText(R.id.tv_info, text);
    }

    public void setIcon(Bitmap bitmap) {
        setImageViewBitmap(R.id.iv_cover, bitmap != null ? bitmap : bmSong);
    }

    public void setPlay(boolean playing) {
        setImageViewBitmap(R.id.btn_play, playing ? bmPause : bmPlay);
    }

    public void setSong(SongItem song) {
        if(song != null) {
            setTitle(song.getName());
            setInfo(song.getArtistName());
            setIcon(song.getBitmap());
        }
        else {
            setTitle(null);
            setInfo(null);
            setIcon(null);
        }
    }

    private void setTextViewText(int viewId, String text) {
        notification.contentView.setTextViewText(viewId, text);
        if(currentVersionSupportBigNotification)
            notification.bigContentView.setTextViewText(viewId, text);
    }

    private void setImageViewBitmap(int viewId, Bitmap bitmap) {
        notification.contentView.setImageViewBitmap(viewId, bitmap);
        if(currentVersionSupportBigNotification)
            notification.bigContentView.setImageViewBitmap(viewId, bitmap);
    }

    private void setListeners(Context context) {
        setListeners(context, notification.contentView);
        if(currentVersionSupportBigNotification)
            setListeners(context, notification.bigContentView);
    }

    private void setListeners(Context context, RemoteViews view) {
        Intent previous = new Intent(PlayerReceiver.ACTION_PREVIOUS);
        Intent next = new Intent(PlayerReceiver.ACTION_NEXT);
        Intent play = new Intent(PlayerReceiver.ACTION_PLAY);

        PendingIntent pPrevious = PendingIntent.getBroadcast(context.getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btn_previous, pPrevious);

        PendingIntent pNext = PendingIntent.getBroadcast(context.getApplicationContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btn_next, pNext);

        PendingIntent pPlay = PendingIntent.getBroadcast(context.getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btn_play, pPlay);
    }

}
