package com.rikkeisoft.musicplayer.player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.MainActivity;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.player.PlayerReceiver;
import com.rikkeisoft.musicplayer.utils.AppUtils;

public class PlayerNotification {

    private static boolean currentVersionSupportBigNotification = false;
    private static boolean currentVersionSupportLockScreenControls = AppUtils.currentVersionSupportLockScreenControls();

    private Notification notification;

    private Resources resources;

    public PlayerNotification(Context context) {

        this.resources = context.getResources();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if(notificationManager != null) {
                NotificationChannel channel = new NotificationChannel("com.rikkeisoft.musicplayer", "Beauty Music", NotificationManager.IMPORTANCE_DEFAULT);
//                channel.setDescription("channel_description");
                notificationManager.createNotificationChannel(channel);
            }
        }

        // The PendingIntent to launch our activity if the user selects this notification
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        intent = new Intent(context, PlayerReceiver.class);
        intent.setAction(PlayerReceiver.ACTION_DELETE_NOTIFICATION);
        PendingIntent pDelete = PendingIntent.getBroadcast(context, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "com.rikkeisoft.musicplayer")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...")
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setDeleteIntent(pDelete)
                .setWhen(0)
                .setCustomContentView(new RemoteViews(context.getApplicationContext().getPackageName(), R.layout.notification_player));

        if(currentVersionSupportBigNotification)
            mBuilder.setCustomBigContentView(new RemoteViews(context.getApplicationContext().getPackageName(), R.layout.notification_big_player));

        notification = mBuilder.build();

        setListeners(context, notification.contentView);

        if(currentVersionSupportBigNotification) setListeners(context, notification.bigContentView);

        setTitle("");
        setInfo("");

    }

    public Notification getNotification() {
        return notification;
    }

    public void setTitle(String text) {
        notification.contentView.setTextViewText(R.id.tv_title, text);
        if(currentVersionSupportBigNotification)
            notification.bigContentView.setTextViewText(R.id.tv_title, text);
    }

    public void setInfo(String text) {
        notification.contentView.setTextViewText(R.id.tv_info, text);
        if(currentVersionSupportBigNotification)
            notification.bigContentView.setTextViewText(R.id.tv_info, text);
    }

    public void setIcon(Bitmap bitmap) {
        if(bitmap != null) notification.contentView.setImageViewBitmap(R.id.iv_cover, bitmap);
        else notification.contentView.setImageViewResource(R.id.iv_cover, R.drawable.im_song);
    }

    public void setPlay(boolean playing) {
        notification.contentView.setImageViewResource(R.id.btn_play,
                playing ? R.drawable.ic_pause : R.drawable.ic_play);
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
