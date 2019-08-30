package com.github.tj;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/***
 *   created by android on 2019/8/29
 */
public class TJ {
    public static void init(Activity activity){
        if(activity==null){
            throw new IllegalStateException("activity can not null");
        }
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
            }
            @Override
            public void onActivityPaused(Activity activity) {
                LG.e("onActivityPaused:"+activity.getClass().getSimpleName());
            }
            @Override
            public void onActivityStopped(Activity activity) {
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
