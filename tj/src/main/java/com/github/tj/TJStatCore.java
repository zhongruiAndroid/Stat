package com.github.tj;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/***
 *   created by android on 2019/8/29
 */
public class TJStatCore {
    private String logId;
    private final int cacheSize=5;
    private static TJStatCore singleObj;
    private PageBean pageBeanAct;
    private List pageList;
    private SparseArrayCompat<PageBean> pageBeanFragList;

    private TJStatCore() {
        pageList=new ArrayList();
        pageBeanAct =new PageBean();
        pageBeanFragList =new SparseArrayCompat<>();
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
        StringBuilder stringBuilder=new StringBuilder(Calendar.getInstance().getTimeInMillis()+"");
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
        } catch ( Exception e) {
            e.printStackTrace();
            return UUID.randomUUID().toString();
        }
        //对生成的16字节数组进行补零操作
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10){
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
    public void onResume(Activity activity) {
        onResume(activity,null);
    }
    public void onPause(Activity activity) {
        onPause(activity,null);
    }
    public void onResume(Activity activity,String pageName) {
        if(activity==null){
            throw new IllegalStateException("onResume() activity不能为空");
        }
        setTopAct(activity);
        String className = activity.getClass().getSimpleName();
        if(pageName==null){
            pageName=className;
        }
        long intoTime = Calendar.getInstance().getTimeInMillis();

        //进入一个页面时，如果存在endtime说明需要把上个页面数据放到list里面去
        if(pageBeanAct.end_time>0){
            pageList.add(pageBeanAct);
            //如果数据超过5个就放到数据库里面去
            if(pageList.size()>cacheSize){
                saveDataToDataBase(activity);
            }
        }else{
            //没有endtime说明第一次进入
            pageBeanAct.page_type=1;
        }
        //获取上一个页面name
        String prePageName=pageBeanAct.page_name;

        //需要重置属性，保存新页面数据
        pageBeanAct.reset();

        pageBeanAct.page_prev=prePageName;
        pageBeanAct.page_name=className;
        pageBeanAct.page_nick_name=pageName;
        pageBeanAct.begin_time=intoTime;
        pageBeanAct.log_id=logId;
    }
    public void onPause(Activity activity,String pageName) {
        if(activity==null){
            throw new IllegalStateException("onPause() activity不能为空");
        }
        String className = activity.getClass().getSimpleName();
        if(pageName==null){
            pageName=className;
        }
        long outTime = Calendar.getInstance().getTimeInMillis();
        pageBeanAct.end_time=outTime;
    }
    /******************************************************************/

    public void onResume(Fragment fragment) {
        onResume(fragment,fragment.getClass().getSimpleName());
    }
    public void onPause(Fragment fragment) {
        onPause(fragment,fragment.getClass().getSimpleName());
    }
    public void onResume(Fragment fragment,String pageName) {
        if(fragment==null){
            throw new IllegalStateException("onResume() fragment不能为空");
        }
        long intoTime = Calendar.getInstance().getTimeInMillis();
        String className = fragment.getClass().getSimpleName();
        if(pageName==null){
            pageName=className;
        }
        //一个activity里面可以有多个fragment，所以把fragment的页面信息提前储存到list里面
        int hashCode = fragment.getClass().getName().hashCode();
        PageBean pageBean = pageBeanFragList.get(hashCode);
        if(pageBean==null){
            pageBean=new PageBean();
        }
        //获取activity上一个页面name
        String prePageName=pageBeanAct.page_prev;
        pageBean.page_prev=prePageName;
        pageBean.page_name=className;
        pageBean.page_nick_name=pageName;
        pageBean.begin_time=intoTime;
        pageBean.log_id=logId;

        pageBeanFragList.put(hashCode,pageBean);
        if(pageBean.end_time>0&&pageBeanFragList.size()>cacheSize){
            //如果数据超过5个就放到数据库里面去
            saveDataToDataBaseForFragment(fragment.getActivity());
        }
    }
    public void onPause(Fragment fragment,String pageName) {
        if(fragment==null){
            throw new IllegalStateException("onPause() fragment不能为空");
        }
        int hashCode = fragment.getClass().getName().hashCode();
        PageBean pageBean = pageBeanFragList.get(hashCode);
        if(pageBean==null){
            return;
        }
        long outTime = Calendar.getInstance().getTimeInMillis();
        pageBean.end_time=outTime;
    }



    //如果app切换到后台，也要放到数据库里面去，防止用户从任务管理器关闭app
    public void saveDataToDataBase(Context context){
        boolean addResult = SaveHelper.saveDataToSqlLite(context, pageList);
        if(addResult){
            pageList.clear();
        }
        //添加Activity的同时也把fragment添加进去
        saveDataToDataBaseForFragment(context);
    }
    public void saveDataToDataBaseForFragment(Context context){
        boolean addResult = SaveHelper.addDataForFragment(context, pageBeanFragList);
        if(addResult){
            pageBeanFragList.clear();
        }
    }

    public void setExitFlag() {
        pageBeanAct.page_type=2;
    }
    public void removeExitFlag() {
        pageBeanAct.page_type=0;
    }
}
