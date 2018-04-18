package com.rikkeisoft.musicplayer.player;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;

import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.utils.AppUtils;

public class LockScreenPlayer {

    private static boolean currentVersionSupportLockScreenControls = AppUtils.currentVersionSupportLockScreenControls();

    private RemoteControlClient remoteControlClient;

    public LockScreenPlayer(Context context, ComponentName remoteComponentName) {

        registerRemoteControlClient(context, remoteComponentName);
    }

    private void registerRemoteControlClient(Context context, ComponentName remoteComponentName){
        if(!currentVersionSupportLockScreenControls || remoteControlClient != null) return;

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(audioManager != null) {
            Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            mediaButtonIntent.setComponent(remoteComponentName);

            PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(context, 0, mediaButtonIntent, 0);

            remoteControlClient = new RemoteControlClient(mediaPendingIntent);

            remoteControlClient.setTransportControlFlags(
                    RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
//                        RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
//                        RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
//                        RemoteControlClient.FLAG_KEY_MEDIA_STOP |
                    RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
                    RemoteControlClient.FLAG_KEY_MEDIA_NEXT);

            audioManager.registerRemoteControlClient(remoteControlClient);
        }
    }

    public void unregisterRemoteControlClient(Context context) {
        if(!currentVersionSupportLockScreenControls || remoteControlClient == null) return;

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(audioManager != null) audioManager.unregisterRemoteControlClient(remoteControlClient);
    }

    public void updateMetadata(Context context, SongItem song){
        if (!currentVersionSupportLockScreenControls || remoteControlClient == null || song == null) return;

        RemoteControlClient.MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, song.getAlbumName());
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, song.getArtistName());
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, song.getName());
        // FileNotFoundException
//        metadataEditor.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, song.cloneBitmap());
        metadataEditor.apply();

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(audioManager != null) audioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
//                Log.d("debug", "---onAudioFocusChange " + focusChange);

            }
        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }
}
