package com.rikkeisoft.musicplayer.player;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.support.v4.media.MediaBrowserCompat;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.model.item.SongItem;

public class PlayerLockScreen {
    RemoteControlClient remoteControlClient;

    public PlayerLockScreen(Context context) {

        registerRemoteClient(context);
    }

    private void registerRemoteClient(Context context){
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if(remoteControlClient == null && audioManager != null) {
                ComponentName remoteComponentName = new ComponentName(
                        context.getApplicationContext(), PlayerReceiver.class);
                audioManager.registerMediaButtonEventReceiver(remoteComponentName);

                Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                mediaButtonIntent.setComponent(remoteComponentName);

                PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(context, 0, mediaButtonIntent, 0);
                remoteControlClient = new RemoteControlClient(mediaPendingIntent);
                audioManager.registerRemoteControlClient(remoteControlClient);


                remoteControlClient.setTransportControlFlags(
                        RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
                                RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                                RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
                                RemoteControlClient.FLAG_KEY_MEDIA_STOP |
                                RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
                                RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateMetadata(Context context, SongItem song){
        if (remoteControlClient == null) return;

        RemoteControlClient.MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, song.getAlbumName());
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, song.getArtistName());
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, song.getName());
        metadataEditor.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, song.getBitmap());
        metadataEditor.apply();

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {

            }
        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }
}
