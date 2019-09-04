package com.github.tj;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;
import android.text.TextUtils;

import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

/***
 *   created by android on 2019/8/29
 */
public class TJStatCore {
    private String logId;
    private final int cacheSize = 10;
    private static TJStatCore singleObj;
    private PageBean pageBeanBefore;//上一个act页面
    private PageBean pageBeanAct;//当前act页面


    //判断fragment是否发生了页面切换
    private boolean isChangePage=true;
    private PageBean pageBeanBeforeFrag;//上一个frag页面
    private PageBean pageBeanFrag;//当前frag页面

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

    public Activity getTopAct() {
        return topAct;
    }

    public void setTopAct(Activity topAct) {
        this.topAct = topAct;
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

    public void onResume(Activity activity) {
        onResume(activity, null);
    }

    public void onPause(Activity activity) {
        onPause(activity, null);
    }

    public void onResume(Activity activity, String pageName) {
        if (activity == null) {
            throw new IllegalStateException("onResume() activity不能为空");
        }
        //设置最上层页面Activity
        setTopAct(activity);
        String className = activity.getClass().getSimpleName();
        if (pageName == null) {
            pageName = className;
        }
        long intoTime = Calendar.getInstance().getTimeInMillis();
        /**********第1种情况************/
        if (pageBeanBefore == null) {
            //如果没有before页面，则为启动app进入的第一个界面,此时改变logid
            changeLogId();
            setDataForPage(pageBeanAct, 1, "", className, pageName, intoTime);
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

        /**********第2种情况************/
        if (pageBeanBefore.page_name.equals(className)) {
            //app从后台回到当前页面,此时改变logid
            changeLogId();
            pageBeanBefore = null;
            pageBeanAct.reset();
            setDataForPage(pageBeanAct, 1, "", className, pageName, intoTime);
            return;
        }


        /**********第4种情况************/
        //进入app之后的页面跳转
        //需要重置属性，保存新页面数据
        pageBeanAct.reset();
        //pageBeanBefore.page_name获取上一个页面name
        setDataForPage(pageBeanAct, PageBean.PAGE_TYPE_OTHER,pageBeanBefore.page_name, className, pageName, intoTime);
    }

    public void onPause(Activity activity, String pageName) {
        if (activity == null) {
            throw new IllegalStateException("onPause() activity不能为空");
        }
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
            SaveHelper.addData(activity, advertPage);
            return;
        }*/

        pageBeanAct.end_time = outTime;
        //跳转页面，或者结束页面时，将当前页面设置为上一个页面
        if (pageBeanBefore == null) {
            pageBeanBefore = new PageBean();
        }
        nowPageCopyToBefore(pageBeanAct, pageBeanBefore);

        //离开时保存页面数据
        SaveHelper.addData(activity, pageBeanBefore);
    }

    private void nowPageCopyToBefore(PageBean now, PageBean before) {
        before.uid = now.uid;
        before.create_time = now.create_time;
        before.page_name = now.page_name;
        before.page_prev = now.page_prev;
        before.page_nick_name = now.page_nick_name;
        before.begin_time = now.begin_time;
        before.end_time = now.end_time;
        before.log_id = now.log_id;
        before.page_type = now.page_type;
        before.page_param1 = now.page_param1;
        before.page_param2 = now.page_param2;
        before.page_param3 = now.page_param3;
        before.data_flag = now.data_flag;
    }

    /**
     * @param pageBeanAct
     * @param pageType     1:第一次启动时，2:最后退出时，3:中间 4:既是第一次启动又是最后退出,
     * @param pagePrev     上一个界面
     * @param pageName     当前界面
     * @param pageNickName 当前界面备注
     * @param beginTime    进入时间
     */
    private void setDataForPage(PageBean pageBeanAct, int pageType, String pagePrev, String pageName, String pageNickName, long beginTime) {
        pageBeanAct.page_type = pageType;
        pageBeanAct.page_prev = pagePrev;
        pageBeanAct.page_name = pageName;
        pageBeanAct.page_nick_name = pageNickName;
        pageBeanAct.begin_time = beginTime;
        pageBeanAct.log_id = logId;
    }

    /******************************************************************/

    public void onResume(Fragment fragment) {
        onResume(fragment, fragment.getClass().getSimpleName());
    }

    public void onPause(Fragment fragment) {
        onPause(fragment, fragment.getClass().getSimpleName());
    }

    public void onResume(Fragment fragment, String pageName) {
        if (fragment == null) {
            throw new IllegalStateException("onResume() fragment不能为空");
        }
        if(isChangePage){
            isChangePage=!isChangePage;
        }else{
            //单个activity多个fragment同时显示时，只记录第一个
            return;
        }
        String className = fragment.getClass().getSimpleName();
        if (pageName == null) {
            pageName = className;
        }
        long intoTime = Calendar.getInstance().getTimeInMillis();
        /**********第1种情况************/

        if (pageBeanBeforeFrag == null) {
            setDataForPage(pageBeanFrag, 1, "", className, pageName, intoTime);
            return;
        }
        /* *//**********第3种情况************//*
        //热启动显示广告页的时候，这个时候不将广告页面作为启动页,属于定制性业务需求
         沟通结果：不考虑广告页和启动页面
        }*/

        /**********第2种情况************/
        if (pageBeanBeforeFrag.page_name.equals(className)) {
            pageBeanBeforeFrag = null;
            pageBeanFrag.reset();
            setDataForPage(pageBeanFrag, 1, "", className, pageName, intoTime);
            return;
        }


        /**********第4种情况************/
        //进入app之后的页面跳转
        //需要重置属性，保存新页面数据
        pageBeanFrag.reset();
        //pageBeanBefore.page_name获取上一个页面name
        setDataForPage(pageBeanFrag, PageBean.PAGE_TYPE_OTHER,pageBeanBeforeFrag.page_name, className, pageName, intoTime);
    }

    public void onPause(Fragment fragment, String pageName) {
        if (fragment == null) {
            throw new IllegalStateException("onPause() fragment不能为空");
        }
        isChangePage =true;
        long outTime = Calendar.getInstance().getTimeInMillis();
        pageBeanFrag.end_time = outTime;
        //跳转页面，或者结束页面时，将当前页面设置为上一个页面
        if (pageBeanBeforeFrag == null) {
            pageBeanBeforeFrag = new PageBean();
        }
        nowPageCopyToBefore(pageBeanFrag, pageBeanBeforeFrag);

        //离开时保存页面数据
        SaveHelper.addData(fragment.getActivity(), pageBeanBeforeFrag);

    }



    public void setExitFlag(Context context) {
        //如果app开始处在后台，就把当前页设置为最后退出页(同时也有可能是第一次启动页)
        // 1:第一次启动时，2:最后退出时，3:既是第一次启动又是最后退出,0:默认
        if (pageBeanAct.page_type == PageBean.PAGE_TYPE_INTO) {
            pageBeanAct.page_type = PageBean.PAGE_TYPE_INTO_OUT;
        } else {
            pageBeanAct.page_type = PageBean.PAGE_TYPE_OUT;
        }
        SaveHelper.updateData(context, pageBeanAct);

        //如果当前activity有fragment页面信息，也保存
        if(pageBeanFrag!=null&&TextUtils.isEmpty(pageBeanFrag.page_name)==false){
            //fragment页面的type跟随当前activity
            pageBeanFrag.page_type=pageBeanAct.page_type;
            SaveHelper.updateData(context, pageBeanFrag);
        }
    }


}
