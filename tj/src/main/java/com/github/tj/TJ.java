package com.github.tj;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

/***
 *   created by android on 2019/8/29
 */
public class TJ {
    public static void onResume(Activity activity) {
        onResume(activity,null);
    }
    public static synchronized void onResume(Activity activity,String name) {
        TJStatCore.get().onResume(activity,name);
    }
    public static  void onPause(Activity activity) {
        onPause(activity,null);
    }
    public static synchronized void onPause(Activity activity,String name) {
        TJStatCore.get().onPause(activity,name);
    }

    public static void onResume(Fragment fragment) {
        onResume(fragment,null);
    }
    public static synchronized void onResume(Fragment fragment,String name) {
        TJStatCore.get().onResume(fragment,name);
    }
    public static void onPause(Fragment fragment) {
        onPause(fragment,null);
    }
    public static synchronized void onPause(Fragment fragment,String name) {
        TJStatCore.get().onPause(fragment,name);
    }
    public static  void setLogId(String logId) {
        TJStatCore.get().setLogId(logId);
    }
    public static  void changeLogId() {
        TJStatCore.get().changeLogId();
    }



    public static void init(Activity activity){
        if(activity==null){
            throw new IllegalStateException("init() activity can not null");
        }
        //每次启动app应用之后的页面信息上报，接口需要一个logid
        TJStatCore.get().changeLogId();
        Application application = activity.getApplication();
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                LG.e("onActivityCreated:"+activity.getClass().getSimpleName());
            }
            @Override
            public void onActivityStarted(Activity activity) {
                LG.e("onActivityStarted:"+activity.getClass().getSimpleName());
            }
            @Override
            public void onActivityResumed(Activity activity) {
                LG.e("onActivityResumed:"+activity.getClass().getSimpleName());
                Activity topAct = TJStatCore.get().getTopAct();
                if(topAct!=null&&topAct==activity){
                    //如果当前页面的activity进入stop状态，将页面标记改为最后退出状态，防止用户从任务管理器关闭app，如果没有关闭，则在resumed改回状态
                    TJStatCore.get().removeExitFlag();
                }
            }
            @Override
            public void onActivityPaused(Activity activity) {
                LG.e("onActivityPaused:"+activity.getClass().getSimpleName());
            }
            @Override
            public void onActivityStopped(Activity activity) {
                Activity topAct = TJStatCore.get().getTopAct();
                if(topAct!=null&&topAct==activity){
                    //如果当前页面的activity进入stop状态，将页面标记改为最后退出状态，防止用户从任务管理器关闭app，如果没有关闭，则在resumed改回状态
                    //并且将数据保存到数据中
                    TJStatCore.get().saveDataToDataBase(activity);
                    TJStatCore.get().setExitFlag();
                }
                LG.e("onActivityStopped:"+activity.getClass().getSimpleName());
            }
            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                LG.e("onActivitySaveInstanceState:"+activity.getClass().getSimpleName());
            }
            @Override
            public void onActivityDestroyed(Activity activity) {
                LG.e("onActivityDestroyed:"+activity.getClass().getSimpleName());
            }
        });
    }


}
