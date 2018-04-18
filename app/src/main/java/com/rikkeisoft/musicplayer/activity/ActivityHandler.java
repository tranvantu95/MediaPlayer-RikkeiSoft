package com.rikkeisoft.musicplayer.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

public class ActivityHandler extends AppCompatActivity {

    public static final String ACTION_SHOW_PLAYER_ACTIVITY = "com.rikkeisoft.musicplayer.action.SHOW_PLAYER_ACTIVITY";

    public static final int FLAG_OPEN_PLAYER = 1;
    public static String FLAG = "flag";

    private boolean handled;

    public static Intent createIntent(Context context, int flag) {
        Intent intent = new Intent(context, ActivityHandler.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(FLAG, flag);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("debug", "---onCreate " + getClass().getSimpleName());

        // If this activity is the root activity of the task, the app is not running
//        if (isTaskRoot()) {
//            // Start the app before finishing
//            int flag = getIntent().getIntExtra(FLAG, 0);
//
//            switch (flag) {
//                case FLAG_OPEN_PLAYER:
//                    openPlayerFromRoot();
//                    break;
//            }
//
//            handled = true;
//        }

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("debug", "---onDestroy " + getClass().getSimpleName());

//        if(handled) return;

        int flag = getIntent().getIntExtra(FLAG, 0);

        switch (flag) {
            case FLAG_OPEN_PLAYER:
                openPlayer();
                break;

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("debug", "---onNewIntent " + getClass().getSimpleName());
    }

    private void openPlayerFromRoot() {
        Intent intent = MainActivity.createIntent(this, FLAG_OPEN_PLAYER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        startActivity(intent);
    }

    private void openPlayer() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null) {
            List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(1);
//            Log.d("debug", "taskInfoList " + taskInfoList.size());
            ActivityManager.RunningTaskInfo taskInfo = taskInfoList.get(0);
//            Log.d("debug", "numActivities " + taskInfo.numActivities);
//            Log.d("debug", "numRunning " + taskInfo.numRunning);
//            Log.d("debug", "baseActivity " + taskInfo.baseActivity.getClassName());
//            Log.d("debug", "topActivity " + taskInfo.topActivity.getClassName());

            if(taskInfo.topActivity.getClassName().contains("Launcher")) {
                openPlayerFromRoot();
            }
            else
                if(!PlayerActivity.class.getName().equals(taskInfo.topActivity.getClassName()))
                    startActivity(PlayerActivity.createIntent(this));
        }
        else startActivity(PlayerActivity.createIntent(this));
    }
}
