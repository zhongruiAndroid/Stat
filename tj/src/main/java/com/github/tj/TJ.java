package com.github.tj;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/***
 *   created by android on 2019/8/29
 */
public class TJ implements Serializable {
    //广告页面
    public static final String TJ_IGNORE_ADVERT_PAGE = "TJ_IGNORE_ADVERT_PAGE";
//    public static final String TJ_IGNORE_SPLASH_PAGE = "TJ_IGNORE_SPLASH_PAGE";

    public static void onResume(Activity activity) {
        onResume(activity, null);
    }

    public static synchronized void onResume(Activity activity, String name) {
        TJStatCore.get().onResume(activity, name);
    }

    public static void onPause(Activity activity) {
        onPause(activity, null);
    }

    public static synchronized void onPause(Activity activity, String name) {
        TJStatCore.get().onPause(activity, name);
    }

    public static void onResume(Fragment fragment) {
        onResume(fragment, null);
    }

    public static synchronized void onResume(Fragment fragment, String name) {
        TJStatCore.get().onResume(fragment, name);
    }

    public static void onPause(Fragment fragment) {
        onPause(fragment, null);
    }

    public static synchronized void onPause(Fragment fragment, String name) {
        TJStatCore.get().onPause(fragment, name);
    }

    public static void setLogId(String logId) {
        TJStatCore.get().setLogId(logId);
    }

    public static void changeLogId() {
        TJStatCore.get().changeLogId();
    }

    public static void setIgnorePageName(String pageName){
        TJStatCore.get().setIgnorePageName(pageName);
    }
    public static void init(final Context context) {
        if (context == null) {
            throw new IllegalStateException("init() context can not null");
        }
        Application application = null;
        if (context instanceof Application) {
            application = (Application) context;
        } else if (context instanceof Activity) {
            application = ((Activity) context).getApplication();
        }
        //每次启动app应用之后的页面信息上报，接口需要一个logid
        TJStatCore.get().setInit(true);
        TJStatCore.get().changeLogId();
        if (application == null) {
            throw new IllegalStateException("no application");
        }
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Activity topAct = TJStatCore.get().getTopAct();
                if (topAct != null && topAct == activity&&isAppOnForeground(context)==false) {

                    //如果当前页面的activity进入stop状态，将页面标记改为最后退出状态
                    TJStatCore.get().setExitFlag(activity);

                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    public static void setUploadListener(TJUpLoadDataListener tjUpLoadDataListener) {
        TJStatCore.get().setTjUpLoadDataListener(tjUpLoadDataListener);
    }

    public static void deleteData(Context context, List<PageBean> list) {
        if (context == null) {
            throw new IllegalStateException("context is null");
        }
        TJStatCore.get().deleteData(context, list);
    }

    public static void setDeBug(boolean debug) {
        TJStatCore.get().setDeBug(debug);
    }

    /**
     * 页面本地缓存数量，超过数量就上报给接口
     * @param cacheSize
     */
    public void setCacheSize(int cacheSize) {
        TJStatCore.get().setCacheSize(cacheSize);
    }

    /**
     * 广告点击事件缓存数量，超过数量就上报给接口
     * @param clickCacheSize
     */
    public void setClickCacheSize(int clickCacheSize) {
        TJStatCore.get().setClickCacheSize(clickCacheSize);
    }


    /**********************************************广告事件点击上报*******************************************************/
    public static void onAdvertEvent(Fragment fragment, String pageNickName, String clickId, String clickName, String attrJson) {
        if(fragment==null){
            throw new IllegalStateException("onAdvertEvent() fragment not null");
        }
        onAdvertEvent(fragment.getActivity(),pageNickName,clickId,clickName,attrJson);
    }

    public static void onAdvertEvent(Activity activity, String pageNickName, String clickId, String clickName, String attrJson) {
        if(activity==null){
            throw new IllegalStateException("onAdvertEvent() activity not null");
        }
        ClickBean clickBean = new ClickBean();
        clickBean.begin_time = String.valueOf(Calendar.getInstance().getTimeInMillis());
        clickBean.click_id = clickId;
        clickBean.click_name = clickName;
        clickBean.param_attr = attrJson;
        clickBean.page_name = activity.getClass().getSimpleName();
        clickBean.page_nick_name = pageNickName;
        TJStatCore.get().addAdvertClickData(activity,clickBean);
    }


    public static void deleteAdvertClickData(Context context, List<AdvertUploadBean> list) {
        if (context == null) {
            throw new IllegalStateException("context is null");
        }
        TJStatCore.get().deleteAdvertClickData(context, list);
    }
    private static boolean isAppOnForeground(Context context) {
        Context applicationContext = context.getApplicationContext();
        ActivityManager activityManager = (ActivityManager)applicationContext.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = applicationContext.getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)&& appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}
