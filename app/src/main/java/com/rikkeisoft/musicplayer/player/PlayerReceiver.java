package com.rikkeisoft.musicplayer.player;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.rikkeisoft.musicplayer.service.PlayerService;

public class PlayerReceiver extends BroadcastReceiver {

    public static final String ACTION_PLAY_PAUSE = "com.rikkeisoft.musicplayer.action.PLAYER_PLAY_PAUSE";
    public static final String ACTION_PREVIOUS = "com.rikkeisoft.musicplayer.action.PLAYER_PREVIOUS";
    public static final String ACTION_NEXT = "com.rikkeisoft.musicplayer.action.PLAYER_NEXT";

    public static final String ACTION_DELETE_NOTIFICATION = "com.rikkeisoft.musicplayer.action.DELETE_NOTIFICATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        PlayerService.LocalBinder binder = (PlayerService.LocalBinder)
                peekService(context, new Intent(context, PlayerService.class));
        PlayerService playerService = binder != null ? binder.getService() : null;
        PlaylistPlayer playlistPlayer = playerService != null ? playerService.getPlaylistPlayer() : null;

        handleAction(playerService, playlistPlayer, intent, intent.getAction());
    }

    private void handleAction(PlayerService playerService, PlaylistPlayer playlistPlayer, Intent intent, String action) {
        if(ACTION_PLAY_PAUSE.equals(action)) {
            Log.d("debug", "ACTION_PLAY_PAUSE " + getClass().getSimpleName());
            if(playlistPlayer != null) playlistPlayer.togglePlay();
        }
        else if(ACTION_PREVIOUS.equals(action)) {
            Log.d("debug", "ACTION_PREVIOUS " + getClass().getSimpleName());
            if(playlistPlayer != null) playlistPlayer.previous();
        }
        else if(ACTION_NEXT.equals(action)) {
            Log.d("debug", "ACTION_NEXT " + getClass().getSimpleName());
            if(playlistPlayer != null) playlistPlayer.next();
        }
        else if(ACTION_DELETE_NOTIFICATION.equals(action)) {
            Log.d("debug", "ACTION_DELETE_NOTIFICATION " + getClass().getSimpleName());
            if(playerService != null) playerService.onDeleteNotification();
        }
        else if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
            Log.d("debug", "ACTION_AUDIO_BECOMING_NOISY " + getClass().getSimpleName());

            /*
            *   When a headset is unplugged or a Bluetooth device disconnected,
            *   the audio stream automatically reroutes to the built-in speaker.
            *   If you listen to music at a high volume, this can be a noisy surprise.
            */

            if(playlistPlayer != null && PlayerSettings.autoPauseWhenHeadsetUnplugged) playlistPlayer.pause();
        }
        else if(Intent.ACTION_MEDIA_BUTTON.equals(action)) {
            Bundle bundle = intent.getExtras();
            if(bundle == null) return;

            KeyEvent keyEvent = (KeyEvent) bundle.get(Intent.EXTRA_KEY_EVENT);
            if(keyEvent == null) return;

            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN) return;

            Log.d("debug", "ACTION_MEDIA_BUTTON " + keyEvent.getKeyCode() + " " + getClass().getSimpleName());

            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                    Log.d("debug", "KEYCODE_HEADSETHOOK");
                    if(playlistPlayer != null) playlistPlayer.togglePlay();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    Log.d("debug", "KEYCODE_MEDIA_PLAY_PAUSE");
                    if(playlistPlayer != null) playlistPlayer.togglePlay();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    Log.d("debug", "KEYCODE_MEDIA_PLAY");
                    if(playlistPlayer != null) playlistPlayer.resume();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    Log.d("debug", "KEYCODE_MEDIA_PAUSE");
                    if(playlistPlayer != null) playlistPlayer.pause();
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    Log.d("debug", "KEYCODE_MEDIA_STOP");
                    if(playlistPlayer != null) playlistPlayer.stop();
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    Log.d("debug", "KEYCODE_MEDIA_NEXT");
                    if(playlistPlayer != null) playlistPlayer.next();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    Log.d("debug", "KEYCODE_MEDIA_PREVIOUS");
                    if(playlistPlayer != null) playlistPlayer.previous();
                    break;
            }
        }
    }
}
