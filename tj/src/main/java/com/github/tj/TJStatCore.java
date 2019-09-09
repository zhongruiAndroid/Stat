package com.github.tj;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/***
 *   created by android on 2019/8/29
 */
public class TJStatCore implements Serializable {
    private boolean isInit;
    private boolean isDebug = false;
    private String logId;
    //记录页面储存次数
    private int addDataCount;
    private int cacheSize = 10;

    //记录点击次数
    private int clickCount;
    private int clickCacheSize = 5;
    private static TJStatCore singleObj;
    private PageBean pageBeanBefore;//上一个act页面
    private PageBean pageBeanAct;//当前act页面
    private boolean isFirstIntoApp = true;
    //当前线程池保证线程顺序执行
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    //判断fragment是否发生了页面切换
    private boolean isChangePage = true;
    private PageBean pageBeanBeforeFrag;//上一个frag页面
    private PageBean pageBeanFrag;//当前frag页面

    private String ignorePageName = "";
//    private PageBean advertPage;
    //    private List pageList;

    private TJStatCore() {
//        pageList=new ArrayList();
        pageBeanAct = new PageBean();
        pageBeanFrag = new PageBean();
    }

    public static TJStatCore get() {
        if (singleObj == null) {
            synchronized (TJStatCore.class) {
                if (singleObj == null) {
                    singleObj = new TJStatCore();
                }
            }
        }
        return singleObj;
    }

    private Activity topAct;
    private TJUpLoadDataListener tjUpLoadDataListener;

    public Activity getTopAct() {
        return topAct;
    }

    public void setTopAct(Activity topAct) {
        this.topAct = topAct;
    }

    public void setTjUpLoadDataListener(TJUpLoadDataListener tjUpLoadDataListener) {
        this.tjUpLoadDataListener = tjUpLoadDataListener;
    }

    public void changeLogId() {
        StringBuilder stringBuilder = new StringBuilder(Calendar.getInstance().getTimeInMillis() + "");
        stringBuilder.append(new Random().nextInt(1000));
        this.logId = md5Decode(stringBuilder.toString());
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    private String md5Decode(String content) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(content.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return UUID.randomUUID().toString();
        }
        //对生成的16字节数组进行补零操作
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public void setIgnorePageName(String ignorePageName) {
        this.ignorePageName = ignorePageName;
    }

    public void onResume(Activity activity) {
        onResume(activity, null);
    }

    public void onPause(Activity activity) {
        onPause(activity, null);
    }

    public void onResume(Activity activity, String pageName) {
        if (isInit() == false) {
            Log.e("TJStatCore", "请调用TJ.init(this)进行初始化");
            return;
        }
        if (activity == null) {
            throw new IllegalStateException("onResume() activity不能为空");
        }
        String className = activity.getClass().getSimpleName();

        if (ignorePageName.equals(className)) {
            //此处代码仅仅是为了满足业务需求，因为如果不排除所忽略页面的之前页面，那么访问次数每次会多增加一次
            //举个例子，app在后台停留的页面为A，此时回到app显示的A页面(访问一次)，自动显示启动页面(同时也是广告)，然后又回到A页面(又访问一次)
            // 这个过程记录了两次，但是需求只需要记录一次，所以只要进入了启动页，就把之前的页面清理掉，从后续进入的页面开始记录
            setFirstInto();
            return;
        }
        //设置最上层页面Activity
        setTopAct(activity);
        if (pageName == null) {
            pageName = className;
        }
        long intoTime = Calendar.getInstance().getTimeInMillis();
        /**********第1种情况************/
        //如果没有before页面，则视为启动app进入的第一个界面
        if (pageBeanBefore == null) {
            pageBeanAct.reset();
            pageBeanAct.actName = className;
            setDataForPage(pageBeanAct, PageBean.PAGE_TYPE_INTO, "", className, pageName, String.valueOf(intoTime));
            return;
        }
        /* *//**********第3种情况************//*
        沟通结果：不考虑广告页和启动页面
        //热启动显示广告页的时候，这个时候不将广告页面作为启动页,属于定制性业务需求
        if (intent != null && intent.getStringExtra(TJ.TJ_IGNORE_ADVERT_PAGE) != null) {
            advertPage = new PageBean();
            if (className.equals(pageName)) {
                pageName = "广告页面";
            }
            advertPage.data_type=PageBean.DATA_TYPE_ADVERT;
            setDataForPage(advertPage, PageBean.PAGE_TYPE_OTHER, "", className, pageName, intoTime);
            return;
        }*/

        /**********第4种情况************/
        //进入app之后的页面跳转
        //需要重置属性，保存新页面数据
        pageBeanAct.reset();
        pageBeanAct.actName = className;
        //pageBeanBefore.page_name获取上一个页面name
        setDataForPage(pageBeanAct, PageBean.PAGE_TYPE_OTHER, pageBeanBefore.page_name, className, pageName, String.valueOf(intoTime));
    }

    public void onPause(Activity activity, String pageName) {
        if (isInit() == false) {
            Log.e("TJStatCore", "请调用TJ.init(this)进行初始化");
            return;
        }
        if (activity == null) {
            throw new IllegalStateException("onPause() activity不能为空");
        }
        String className = activity.getClass().getSimpleName();
        if (ignorePageName.equals(className)) {
            //此处代码仅仅是为了满足业务需求，因为如果不排除所忽略页面的之前页面，那么访问次数每次会多增加一次
            //举个例子，app在后台停留的页面为A，此时回到app显示的A页面(访问一次)，自动显示启动页面(同时也是广告)，然后又回到A页面(又访问一次)
            // 这个过程记录了两次，但是需求只需要记录一次，所以只要进入了启动页，就把之前的页面清理掉，从后续进入的页面开始记录
            setFirstInto();
            return;
        }
        isFirstIntoApp = false;
       /* String className = activity.getClass().getSimpleName();
        if(pageName==null){
            pageName=className;
        }*/
        long outTime = Calendar.getInstance().getTimeInMillis();
        /* *//**********onResume中的第3种情况************//*
        //热启动显示广告页的时候，这个时候不将广告页面作为启动页,属于定制性业务需求
        Intent intent = activity.getIntent();
        if (advertPage != null && intent != null && intent.getStringExtra(TJ.TJ_IGNORE_ADVERT_PAGE) != null) {
            advertPage.end_time = outTime;
            addData(activity, advertPage);
            return;
        }*/

        pageBeanAct.end_time = String.valueOf(outTime);
        //跳转页面，或者结束页面时，将当前页面设置为上一个页面
        if (pageBeanBefore == null) {
            pageBeanBefore = new PageBean();
        }
        nowPageCopyToBefore(pageBeanAct, pageBeanBefore);

        //离开时保存页面数据
        addData(activity, pageBeanBefore);
    }

    private void nowPageCopyToBefore(PageBean now, PageBean before) {
        before.uid = now.uid;
        before.create_time = now.create_time;
        before.page_name = now.page_name;
        before.page_prev = now.page_prev;
        before.page_nick_name = now.page_nick_name;
        before.begin_time = now.begin_time;
        before.end_time = now.end_time;
        before.page_log_id = now.page_log_id;
        before.page_type = now.page_type;
        before.page_param1 = now.page_param1;
        before.page_param2 = now.page_param2;
        before.page_param3 = now.page_param3;
        before.data_flag = now.data_flag;
        before.actName = now.actName;
    }

    /**
     * @param pageBeanAct
     * @param pageType     1:第一次启动时，2:最后退出时，3:中间 4:既是第一次启动又是最后退出,
     * @param pagePrev     上一个界面
     * @param pageName     当前界面
     * @param pageNickName 当前界面备注
     * @param beginTime    进入时间
     */
    private void setDataForPage(PageBean pageBeanAct, String pageType, String pagePrev, String pageName, String pageNickName, String beginTime) {
        pageBeanAct.page_type = pageType;
        pageBeanAct.page_prev = pagePrev;
        pageBeanAct.page_name = pageName;
        pageBeanAct.page_nick_name = pageNickName;
        pageBeanAct.begin_time = beginTime;
        pageBeanAct.page_log_id = logId;
    }

    /******************************************************************/

    public void onResume(Fragment fragment) {
        onResume(fragment, fragment.getClass().getSimpleName());
    }

    public void onPause(Fragment fragment) {
        onPause(fragment, fragment.getClass().getSimpleName());
    }

    public void onResume(Fragment fragment, String pageName) {
        if (isInit() == false) {
            Log.e("TJStatCore", "请调用TJ.init(this)进行初始化");
            return;
        }
        if (fragment == null) {
            throw new IllegalStateException("onResume() fragment不能为空");
        }
        if (isChangePage) {
            isChangePage = !isChangePage;
        } else {
            //单个activity多个fragment同时显示时，只记录第一个
            return;
        }
        String className = fragment.getClass().getSimpleName();
        FragmentActivity activity = fragment.getActivity();
        String activityName = "";
        if (activity != null) {
            activityName = activity.getClass().getSimpleName();
        }
        if (pageName == null) {
            pageName = className;
        }
        long intoTime = Calendar.getInstance().getTimeInMillis();
        /**********第1种情况************/
        //如果没有before页面,且属于当前activity，则为启动app进入的第一个界面
//        同act
        if (pageBeanBeforeFrag == null) {
            pageBeanFrag.reset();
            pageBeanFrag.actName = activityName;

            String pageType = PageBean.PAGE_TYPE_OTHER;
            if (isFirstIntoApp) {
                pageType = PageBean.PAGE_TYPE_INTO;
            }
            setDataForPage(pageBeanFrag, pageType, "", className, pageName, String.valueOf(intoTime));
            return;
        }
        /* *//**********第3种情况************//*
        //热启动显示广告页的时候，这个时候不将广告页面作为启动页,属于定制性业务需求
         沟通结果：不考虑广告页和启动页面
        }*/


        /**********第4种情况************/
        //进入app之后的页面跳转
        //需要重置属性，保存新页面数据
        pageBeanFrag.reset();
        pageBeanFrag.actName = activityName;
        //pageBeanBefore.page_name获取上一个页面name
        setDataForPage(pageBeanFrag, PageBean.PAGE_TYPE_OTHER, pageBeanBeforeFrag.page_name, className, pageName, String.valueOf(intoTime));
    }

    public void onPause(Fragment fragment, String pageName) {
        if (isInit() == false) {
            Log.e("TJStatCore", "请调用TJ.init(this)进行初始化");
            return;
        }
        if (fragment == null) {
            throw new IllegalStateException("onPause() fragment不能为空");
        }
        isChangePage = true;
        long outTime = Calendar.getInstance().getTimeInMillis();
        pageBeanFrag.end_time = String.valueOf(outTime);
        //跳转页面，或者结束页面时，将当前页面设置为上一个页面
        if (pageBeanBeforeFrag == null) {
            pageBeanBeforeFrag = new PageBean();
        }
        nowPageCopyToBefore(pageBeanFrag, pageBeanBeforeFrag);

        //离开时保存页面数据
        addData(fragment.getActivity(), pageBeanBeforeFrag);

    }


    public void setExitFlag(Context context) {
        //如果app开始处在后台，就把当前页设置为最后退出页(同时也有可能是第一次启动页)
        // 1:第一次启动时，2:最后退出时，4:既是第一次启动又是最后退出,3:中间跳转
        if (pageBeanAct.page_type == PageBean.PAGE_TYPE_INTO) {
            pageBeanAct.page_type = PageBean.PAGE_TYPE_INTO_OUT;
        } else {
            pageBeanAct.page_type = PageBean.PAGE_TYPE_OUT;
        }
        updateData(context, pageBeanAct);

        //如果当前activity有fragment页面信息，也保存
        if (pageBeanFrag != null && TextUtils.isEmpty(pageBeanFrag.page_name) == false && pageBeanFrag.actName.equals(pageBeanAct.actName)) {
            //fragment页面的type跟随当前activity
            if (pageBeanFrag.page_type == PageBean.PAGE_TYPE_INTO) {
                pageBeanFrag.page_type = PageBean.PAGE_TYPE_INTO_OUT;
            } else {
                pageBeanFrag.page_type = PageBean.PAGE_TYPE_OUT;
            }
            updateData(context, pageBeanFrag);
        }
        setFirstInto();
        //app进入后台 此时改变logid
        changeLogId();
    }

    private void setFirstInto() {
        pageBeanBefore = null;

        pageBeanBeforeFrag = null;
        isFirstIntoApp = true;
    }

    private void addData(final Context context, final PageBean bean) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                SaveHelper.addData(context, bean);
                if (addDataCount > cacheSize) {
                    //增加数据时，记录增加的数据量，一旦超过10条就上报，可修改数量
                    addDataCount = 0;
                    prepareUpload(context);
                }
            }
        });

    }

    private void updateData(final Context context, final PageBean bean) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                SaveHelper.updateData(context, bean);
            }
        });
    }

    /**
     * 增加数据时，记录增加的数据量，一旦超过10条就上报，可修改数量
     */
    public void upAddDataCount(Context context) {
        addDataCount++;
    }

    private void prepareUpload(final Context context) {
        final List<PageBean> data = SaveHelper.getData(context);
        if (data == null || data.size() == 0) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (tjUpLoadDataListener != null) {
                    tjUpLoadDataListener.uploadPageData(data);
                }
            }
        });
    }

    public void deleteData(final Context context, final List<PageBean> data) {
        if (data == null || data.size() == 0) {
            return;
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                SaveHelper.deleteData(context, data);
            }
        });
    }

    public void addAdvertClickData(final Context context, final ClickBean bean) {
        if (clickCount >= clickCacheSize) {
            clickCount = 0;
            prepareUploadAdvertClickData(context);
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                clickCount++;
                SaveHelper.addAdvertClickData(context, bean);
            }
        });
    }

    private void prepareUploadAdvertClickData(final Context context) {
        final List<AdvertUploadBean> advertClickData = SaveHelper.getAdvertClickData(context);
        if (advertClickData == null || advertClickData.size() == 0) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (tjUpLoadDataListener != null) {
                    tjUpLoadDataListener.uploadAdvertClickData(advertClickData);
                }
            }
        });
    }

    public void deleteAdvertClickData(final Context context, final List<AdvertUploadBean> data) {
        if (data == null || data.size() == 0) {
            return;
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                SaveHelper.deleteAdvertClickData(context, data);
            }
        });
    }

    public void setDeBug(boolean debug) {
        this.isDebug = debug;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }

    public void setCacheSize(int cacheSize) {
        if (cacheSize <= 0) {
            cacheSize = 2;
        }
        this.cacheSize = cacheSize;
    }

    public void setClickCacheSize(int clickCacheSize) {
        if (clickCacheSize <= 0) {
            clickCacheSize = 2;
        }
        this.clickCacheSize = clickCacheSize;
    }
}
