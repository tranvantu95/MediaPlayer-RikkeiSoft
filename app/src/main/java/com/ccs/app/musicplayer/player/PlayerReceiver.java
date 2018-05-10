package com.ccs.app.musicplayer.player;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.ccs.app.musicplayer.R;
import com.ccs.app.musicplayer.service.PlayerService;

public class PlayerReceiver extends BroadcastReceiver {

    private static boolean playing;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("debug", "onReceive " + getClass().getSimpleName());

        PlayerService.LocalBinder binder = (PlayerService.LocalBinder)
                peekService(context, new Intent(context, PlayerService.class));
        PlayerService playerService = binder != null ? binder.getService() : null;
        PlaylistPlayer playlistPlayer = playerService != null ? playerService.getPlaylistPlayer() : null;

        handleAction(playlistPlayer, context, intent, intent.getAction());
    }

    private static void sendToService(Context context, Intent intent) {
        intent.setComponent(new ComponentName(context, PlayerService.class));
        context.startService(intent);
    }

    public static void handleAction(PlaylistPlayer playlistPlayer, Context context, Intent intent, String action) {
        Log.d("debug", "handleAction " + PlayerReceiver.class.getSimpleName());

        if(PlayerService.ACTION_PLAY_PAUSE.equals(action)) {
            Log.d("debug", "ACTION_PLAY_PAUSE " + PlayerReceiver.class.getSimpleName());
            if(playlistPlayer != null) playlistPlayer.togglePlay();
        }
        else if(PlayerService.ACTION_PREVIOUS.equals(action)) {
            Log.d("debug", "ACTION_PREVIOUS " + PlayerReceiver.class.getSimpleName());
            if(playlistPlayer != null) playlistPlayer.previous();
        }
        else if(PlayerService.ACTION_NEXT.equals(action)) {
            Log.d("debug", "ACTION_NEXT " + PlayerReceiver.class.getSimpleName());
            if(playlistPlayer != null) playlistPlayer.next();
        }
        else if(AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
            Log.d("debug", "ACTION_AUDIO_BECOMING_NOISY " + PlayerReceiver.class.getSimpleName());

            /*
            *   When a headset is unplugged or a Bluetooth device disconnected,
            *   the audio stream automatically reroutes to the built-in speaker.
            *   If you listen to music at a high volume, this can be a noisy surprise.
            */

            if(PlayerSettings.autoPauseWhenHeadsetUnplugged)
                if (playlistPlayer != null) playlistPlayer.pause();

        }
        else if(TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(action)) {
            Log.d("debug", "ACTION_PHONE_STATE_CHANGED " + PlayerReceiver.class.getSimpleName());
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if(TelephonyManager.EXTRA_STATE_RINGING.equals(state)){
                Log.d("debug", "EXTRA_STATE_RINGING " + PlayerReceiver.class.getSimpleName());
                if(playlistPlayer != null && playlistPlayer.isRunning()) {
                    playlistPlayer.pause();
                    playing = true;
                }
            }
            else if ((TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state))){
                Log.d("debug", "EXTRA_STATE_OFFHOOK " + PlayerReceiver.class.getSimpleName());
            }
            else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)){
                Log.d("debug", "EXTRA_STATE_IDLE " + PlayerReceiver.class.getSimpleName());
                if(playing && playlistPlayer != null) {
                    playlistPlayer.resume();
                    playing = false;
                }
            }
        }
        else if(Intent.ACTION_MEDIA_BUTTON.equals(action)) {
            Log.d("debug", "ACTION_MEDIA_BUTTON " + PlayerReceiver.class.getSimpleName());

            Bundle bundle = intent.getExtras();
            if(bundle == null) return;

            KeyEvent keyEvent = (KeyEvent) bundle.get(Intent.EXTRA_KEY_EVENT);
            if(keyEvent == null) return;

            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN) return;

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

        if(playlistPlayer == null) sendToService(context, intent);
    }

    public static void setListeners(Context context, RemoteViews view) {
        Intent previous = new Intent(PlayerService.ACTION_PREVIOUS);
        Intent next = new Intent(PlayerService.ACTION_NEXT);
        Intent play = new Intent(PlayerService.ACTION_PLAY_PAUSE);

        PendingIntent pPrevious = PendingIntent.getBroadcast(context.getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btn_previous, pPrevious);

        PendingIntent pNext = PendingIntent.getBroadcast(context.getApplicationContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btn_next, pNext);

        PendingIntent pPlay = PendingIntent.getBroadcast(context.getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btn_play, pPlay);
    }

}
