package com.rikkeisoft.musicplayer.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rikkeisoft.musicplayer.service.PlayerService;
import com.rikkeisoft.musicplayer.player.PlaylistPlayer;

public class PlayerReceiver extends BroadcastReceiver {

    public static final String ACTION_PREVIOUS = "com.rikkeisoft.musicplayer.action.PLAYER_PREVIOUS";
    public static final String ACTION_NEXT = "com.rikkeisoft.musicplayer.action.PLAYER_NEXT";
    public static final String ACTION_PLAY = "com.rikkeisoft.musicplayer.action.PLAYER_PLAY";

    public static final String ACTION_DELETE_NOTIFICATION = "com.rikkeisoft.musicplayer.action.DELETE_NOTIFICATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        PlayerService.LocalBinder binder = (PlayerService.LocalBinder)
                peekService(context, new Intent(context, PlayerService.class));
        PlayerService playerService = binder != null ? binder.getService() : null;
        PlaylistPlayer playlistPlayer = playerService != null ? playerService.getPlaylistPlayer() : null;

        if(ACTION_PREVIOUS.equals(intent.getAction())) {
            Log.d("debug", "ACTION_PREVIOUS " + getClass().getSimpleName());
            if(playlistPlayer != null) playlistPlayer.previous();
        }
        else if(ACTION_NEXT.equals(intent.getAction())) {
            Log.d("debug", "ACTION_NEXT " + getClass().getSimpleName());
            if(playlistPlayer != null) playlistPlayer.next();
        }
        else if(ACTION_PLAY.equals(intent.getAction())) {
            Log.d("debug", "ACTION_PLAY " + getClass().getSimpleName());
            if(playlistPlayer != null) playlistPlayer.togglePlay();
        }
        else if(ACTION_DELETE_NOTIFICATION.equals(intent.getAction())) {
            Log.d("debug", "ACTION_DELETE_NOTIFICATION " + getClass().getSimpleName());
            if(playerService != null) playerService.onDeleteNotification();
        }
    }
}
