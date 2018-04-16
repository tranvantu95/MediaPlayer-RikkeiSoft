package com.rikkeisoft.musicplayer.activity.base;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.app.MyApplication;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.service.PlayerService;
import com.rikkeisoft.musicplayer.utils.General;
import com.rikkeisoft.musicplayer.player.PlaylistPlayer;

public class BaseActivity extends AppCompatActivity {

    // Player Service
    protected PlaylistPlayer playlistPlayer;
    protected PlayerModel playerModel;

    private ServiceConnection playerConnection;

    private void bindPlayerService() {
        if(playerConnection != null) return;
        Log.d("debug", "bindPlayerService " + getClass().getSimpleName());

        playerConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                PlayerService.LocalBinder binder = (PlayerService.LocalBinder) iBinder;
                PlayerService playerService = binder.getService();
                onPlayerServiceConnected(playerService);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {}
        };

        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent, playerConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindPlayerService() {
        if(playerConnection != null) {
            Log.d("debug", "unbindPlayerService " + getClass().getSimpleName());
            unbindService(playerConnection);
            playerConnection = null;
        }
    }

    protected void onPlayerServiceConnected(PlayerService playerService) {
        Log.d("debug", "onPlayerServiceConnected " + getClass().getSimpleName());

        playerService.getLiveData().getPlayerModel().observe(this, new Observer<PlayerModel>() {
            @Override
            public void onChanged(@Nullable PlayerModel playerModel) {
                if(playerModel != null) onPlayerModelCreated(playerModel);
            }
        });

        playerService.getLiveData().getPlaylistPlayer().observe(this, new Observer<PlaylistPlayer>() {
            @Override
            public void onChanged(@Nullable PlaylistPlayer playlistPlayer) {
                onPlaylistPlayerCreated(playlistPlayer);
            }
        });
    }

    protected void onPlayerModelCreated(@NonNull PlayerModel playerModel) {
        Log.d("debug", "onPlayerModelCreated " + getClass().getSimpleName());
        this.playerModel = playerModel;
    }

    protected void onPlaylistPlayerCreated(@Nullable PlaylistPlayer playlistPlayer) {
        Log.d("debug", "onPlaylistPlayerCreated " + getClass().getSimpleName());
        this.playlistPlayer = playlistPlayer;
    }

    // Media change listener
    private BroadcastReceiver onReceiverMediaChange;

    private void registerOnReceiverMediaChange() {
        if(onReceiverMediaChange != null) return;
        Log.d("debug", "registerOnReceiverMediaChange " + getClass().getSimpleName());

        onReceiverMediaChange = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onReceiverMediaChange();
            }
        };

        IntentFilter filter = new IntentFilter(MyApplication.ACTION_MEDIA_CHANGE);
        registerReceiver(onReceiverMediaChange, filter);
    }

    private void unregisterOnReceiverMediaChange() {
        if(onReceiverMediaChange != null) {
            Log.d("debug", "unregisterOnReceiverMediaChange " + getClass().getSimpleName());
            unregisterReceiver(onReceiverMediaChange);
            onReceiverMediaChange = null;
        }
    }

    protected void onReceiverMediaChange() {
        Log.d("debug", "onReceiverMediaChange " + getClass().getSimpleName());

    }

    // Permission
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101;
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

    private boolean showSettings;

    private Dialog requestPermissionRationale;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("debug", "onCreate " + getClass().getSimpleName());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindPlayerService();
        unregisterOnReceiverMediaChange();
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences.Editor editor = getSharedPreferences(MyApplication.DATA, MODE_PRIVATE).edit();
        editor.putBoolean("shouldShowRequestPermissionRationale", General.shouldShowRequestPermissionRationale);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!isShowingGrantPermissionsDialog()) checkPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    onPermissionGranted();
                }
                else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    onPermissionDenied();
                }

                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }

        General.shouldShowRequestPermissionRationale = true;
    }

    protected void checkPermission() {
        Log.d("debug", "checkPermission " + getClass().getSimpleName());
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, permissions[0])
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                onShouldShowRequestPermissionRationale();
            }
            else {
                // No explanation needed; request the permission
                if(General.shouldShowRequestPermissionRationale) onAutoDenyPermission();
                else requestPermissions();

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {
            // Permission has already been granted
            onPermissionGranted();
        }
    }

    protected void requestPermissions() {
        Log.d("debug", "requestPermissions " + getClass().getSimpleName());
        ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
    }

    protected void onAutoDenyPermission() {
        Log.d("debug", "onAutoDenyPermission " + getClass().getSimpleName());
        showRequestPermissionRationale(true);
    }

    protected void onShouldShowRequestPermissionRationale() {
        Log.d("debug", "onShouldShowRequestPermissionRationale " + getClass().getSimpleName());
        showRequestPermissionRationale(false);
    }

    protected void onPermissionGranted() {
        Log.d("debug", "onPermissionGranted " + getClass().getSimpleName());
        General.isPermissionGranted = true;
        hideRequestPermissionRationale();

        registerOnReceiverMediaChange();
        bindPlayerService();
    }

    protected void onPermissionDenied() {
        Log.d("debug", "onPermissionDenied " + getClass().getSimpleName());
        checkPermission();
    }

    protected void showRequestPermissionRationale(boolean _showSettings) {
        showSettings = _showSettings;

        if(requestPermissionRationale != null) {
            if(!requestPermissionRationale.isShowing()) requestPermissionRationale.show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        builder.setTitle(R.string.title_request_permissions);
        builder.setMessage(R.string.message_read_external_storage_permission);

        builder.setNeutralButton(R.string.exit_app, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                exitApp();
            }
        });

        builder.setNegativeButton(R.string.button_grant_permissions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(showSettings) showApplicationSettings();
                else requestPermissions();
            }
        });

        requestPermissionRationale = builder.show();
    }

    protected void hideRequestPermissionRationale() {
        if(requestPermissionRationale != null && requestPermissionRationale.isShowing())
            requestPermissionRationale.hide();
    }

    protected boolean isShowingGrantPermissionsDialog() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(am != null) {
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            if ("com.android.packageinstaller.permission.ui.GrantPermissionsActivity".equals(cn.getClassName())) {
                return true;
            }
        }

        return false;
    }

    protected void showApplicationSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    protected void exitApp() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    // Model
    protected  <Model extends ViewModel> Model getModel(Class<Model> modelClass) {
        return ViewModelProviders.of(this).get(modelClass);
    }

}
