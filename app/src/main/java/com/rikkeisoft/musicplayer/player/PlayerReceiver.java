package com.rikkeisoft.musicplayer.player;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.rikkeisoft.musicplayer.activity.MainActivity;
import com.rikkeisoft.musicplayer.activity.NotificationActivity;
import com.rikkeisoft.musicplayer.activity.PlayerActivity;
import com.rikkeisoft.musicplayer.service.NotificationService;
import com.rikkeisoft.musicplayer.service.PlayerService;
import com.rikkeisoft.musicplayer.player.PlaylistPlayer;
import com.rikkeisoft.musicplayer.utils.AppUtils;

import java.util.List;

public class PlayerReceiver extends BroadcastReceiver {

    public static final String ACTION_PREVIOUS = "com.rikkeisoft.musicplayer.action.PLAYER_PREVIOUS";
    public static final String ACTION_NEXT = "com.rikkeisoft.musicplayer.action.PLAYER_NEXT";
    public static final String ACTION_PLAY = "com.rikkeisoft.musicplayer.action.PLAYER_PLAY";

    public static final String ACTION_DELETE_NOTIFICATION = "com.rikkeisoft.musicplayer.action.DELETE_NOTIFICATION";

    public static final String ACTION_SHOW_PLAYER = "com.rikkeisoft.musicplayer.action.SHOW_PLAYER";

    @Override
    public void onReceive(Context context, final Intent intent) {
        PlayerService.LocalBinder binder = (PlayerService.LocalBinder)
                peekService(context, new Intent(context, PlayerService.class));
        PlayerService playerService = binder != null ? binder.getService() : null;
        PlaylistPlayer playlistPlayer = playerService != null ? playerService.getPlaylistPlayer() : null;

        handleAction(context.getApplicationContext(), playerService, playlistPlayer, intent.getAction());

    }

    private void handleAction(Context context, PlayerService playerService, PlaylistPlayer playlistPlayer, String action) {
        if(ACTION_PREVIOUS.equals(action)) {
            Log.d("debug", "ACTION_PREVIOUS " + getClass().getSimpleName());
            if(playlistPlayer != null) playlistPlayer.previous();
        }
        else if(ACTION_NEXT.equals(action)) {
            Log.d("debug", "ACTION_NEXT " + getClass().getSimpleName());
            if(playlistPlayer != null) playlistPlayer.next();
        }
        else if(ACTION_PLAY.equals(action)) {
            Log.d("debug", "ACTION_PLAY " + getClass().getSimpleName());
            if(playlistPlayer != null) playlistPlayer.togglePlay();
        }
        else if(ACTION_DELETE_NOTIFICATION.equals(action)) {
            Log.d("debug", "ACTION_DELETE_NOTIFICATION " + getClass().getSimpleName());
            if(playerService != null) playerService.onDeleteNotification();
        }
        else if(ACTION_SHOW_PLAYER.equals(action)) {
            Log.d("debug", "ACTION_SHOW_PLAYER " + getClass().getSimpleName());
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if(activityManager != null) {
                List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(Integer.MAX_VALUE);
                Log.d("debug", "taskInfoList " + taskInfoList.size());
                ActivityManager.RunningTaskInfo taskInfo = taskInfoList.get(0);
                Log.d("debug", "numActivities " + taskInfo.numActivities);
                Log.d("debug", "baseActivity " + taskInfo.baseActivity.getClassName());
                Log.d("debug", "topActivity " + taskInfo.topActivity.getClassName());
//                if(!PlayerActivity.class.getName().equals(taskInfo.topActivity.getClassName()))
//                    if("com.android.launcher3.Launcher".equals(taskInfo.baseActivity.getClassName()))
//                        context.startActivity(MainActivity.createIntent(context, MainActivity.OPEN_PLAYER));
//                    else context.startActivity(PlayerActivity.createIntent(context));

                context.startActivity(NotificationActivity.createIntent(context));
            }
            else context.startActivity(PlayerActivity.createIntent(context));
        }
    }
}
