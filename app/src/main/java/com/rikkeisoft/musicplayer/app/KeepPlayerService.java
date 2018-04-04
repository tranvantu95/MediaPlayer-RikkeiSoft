package com.rikkeisoft.musicplayer.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.rikkeisoft.musicplayer.service.PlayerService;

public class KeepPlayerService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "onReceive", Toast.LENGTH_LONG).show();
        if(intent.getAction() != null && intent.getAction().equals(PlayerService.ACTION_KEEP_PLAYER_SERVICE)) {
            Log.d("debug", "onReceive " + getClass().getSimpleName());
//            context.startService(new Intent(context, PlayerService.class));
        }
    }
}
