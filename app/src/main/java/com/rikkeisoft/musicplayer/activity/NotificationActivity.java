package com.rikkeisoft.musicplayer.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, NotificationActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If this activity is the root activity of the task, the app is not running
        if (isTaskRoot()) {
            // Start the app before finishing
            Intent startAppIntent = MainActivity.createIntent(this, MainActivity.OPEN_PLAYER);
            startAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startAppIntent);
        }
        else {
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if(activityManager != null) {
                List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(Integer.MAX_VALUE);
                Log.d("debug", "taskInfoList " + taskInfoList.size());
                for(ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                    Log.d("debug", "numActivities " + taskInfo.numActivities);
                    Log.d("debug", "baseActivity " + taskInfo.baseActivity.getClassName());
                    Log.d("debug", "topActivity " + taskInfo.topActivity.getClassName());
//                if(!PlayerActivity.class.getName().equals(taskInfo.topActivity.getClassName()))
//                    if("com.android.launcher3.Launcher".equals(taskInfo.baseActivity.getClassName()))
//                        context.startActivity(MainActivity.createIntent(context, MainActivity.OPEN_PLAYER));
//                    else context.startActivity(PlayerActivity.createIntent(context));
                }

            }
        }

        finish();
    }
}
