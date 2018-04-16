package com.rikkeisoft.musicplayer.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rikkeisoft.musicplayer.activity.ActivityHandler;
import com.rikkeisoft.musicplayer.service.PlayerService;

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
    }
}
