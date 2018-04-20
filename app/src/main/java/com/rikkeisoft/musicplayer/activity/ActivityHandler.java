package com.rikkeisoft.musicplayer.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.rikkeisoft.musicplayer.utils.AppUtils;

import java.util.List;

public class ActivityHandler extends AppCompatActivity {

    public static final int FLAG_OPEN_PLAYER = 1;
    public static String FLAG = "flag";

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

        finish();
    }

    @Override
    public void finish() {
        super.finish();

        if (isTaskRoot()) {
            int flag = getIntent().getIntExtra(FLAG, 0);

            switch (flag) {
                case FLAG_OPEN_PLAYER:
                    Intent intent = PlayerActivity.createIntent(this);
                    startActivity(AppUtils.makeMainIntent(intent));
                    break;
            }

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("debug", "---onDestroy " + getClass().getSimpleName());

        if (isTaskRoot()) return;

        int flag = getIntent().getIntExtra(FLAG, 0);

        switch (flag) {
            case FLAG_OPEN_PLAYER:
                openPlayer();
                break;

        }
    }

    private void openPlayer() {
        Log.d("debug", "openPlayer " + getClass().getSimpleName());
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null) {
            List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(1);
//            Log.d("debug", "taskInfoList " + taskInfoList.size());
            ActivityManager.RunningTaskInfo taskInfo = taskInfoList.get(0);
//            Log.d("debug", "numActivities " + taskInfo.numActivities);
//            Log.d("debug", "numRunning " + taskInfo.numRunning);
//            Log.d("debug", "baseActivity " + taskInfo.baseActivity.getClassName());
//            Log.d("debug", "topActivity " + taskInfo.topActivity.getClassName());

            if(!PlayerActivity.class.getName().equals(taskInfo.topActivity.getClassName()))
                startActivity(PlayerActivity.createIntent(this));
        }
        else startActivity(PlayerActivity.createIntent(this));
    }
}
