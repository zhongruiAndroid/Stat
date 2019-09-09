package com.lieying.stat;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.tj.LG;
import com.github.tj.TJ;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btJump).setOnClickListener(this);
        isAppOnForeground();
    }
    public boolean isAppOnForeground() {
        // Returns a list of application processes that are running on the
        // device

        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)&& appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btJump:
                /*AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("title");
                builder.setMessage("测试");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();*/
                startActivity(new Intent(this,SecondActivity.class));

            break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        TJ.onResume(this);
        super.onResume();
        LG.e("MainActivity=====================onResume");
    }

    @Override
    protected void onPause() {
        TJ.onPause(this);
        super.onPause();
        LG.e("MainActivity=====================onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LG.e("MainActivity=====================onStop");
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                isAppOnForeground();
            }
        },1000);
    }
}
