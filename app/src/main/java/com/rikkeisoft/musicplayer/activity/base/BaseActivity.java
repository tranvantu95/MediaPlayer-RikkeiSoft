package com.rikkeisoft.musicplayer.activity.base;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.app.MyApplication;
import com.rikkeisoft.musicplayer.utils.Loader;

public class BaseActivity extends AppCompatActivity {

    // Media change listener
    private BroadcastReceiver onReceiverMediaChange;

    private void registerOnReceiverMediaChange() {
        if(onReceiverMediaChange != null) return;

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

    private boolean shouldShowRequestPermissionRationale;
    private boolean showSettings;

    private Dialog requestPermissionRationale;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("debug", "onCreate " + getClass().getSimpleName());

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        shouldShowRequestPermissionRationale = sharedPreferences.getBoolean(
                "shouldShowRequestPermissionRationale", false);

        Loader.initialize(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterOnReceiverMediaChange();
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putBoolean("shouldShowRequestPermissionRationale", shouldShowRequestPermissionRationale);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!isShowingGrantPermissionsDialog()) checkPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

        shouldShowRequestPermissionRationale = true;
    }

    protected void checkPermission() {
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
                if(shouldShowRequestPermissionRationale) onAutoDenyPermission();
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
        ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
    }

    protected void onAutoDenyPermission() {
        showRequestPermissionRationale(true);
    }

    protected void onShouldShowRequestPermissionRationale() {
        showRequestPermissionRationale(false);
    }

    protected void onPermissionGranted() {
        Log.d("debug", "onPermissionGranted");
        hideRequestPermissionRationale();

        registerOnReceiverMediaChange();
    }

    protected void onPermissionDenied() {
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
}
