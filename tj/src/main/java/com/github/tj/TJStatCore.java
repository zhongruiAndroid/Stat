package com.github.tj;

import android.app.Activity;
import android.support.v4.app.Fragment;

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
    private static TJStatCore singleObj;
    private PageBean pageBeanAct;
    private PageBean pageBeanFrag;
    private String logId;
    private List pageList;
    private final int cacheSize=30;

    private TJStatCore() {
        pageList=new ArrayList();
        pageBeanAct =new PageBean();
        pageBeanFrag =new PageBean();
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

        //如果存在endtime说明需要把上个页面数据放到list里面去
        if(pageBeanAct.endTime>0){
            pageList.add(pageBeanAct);
            if(pageList.size()>cacheSize){

            }
        }
        //需要重新实例化保存新数据
        pageBeanAct =new PageBean();
        pageBeanAct.currentPage=className;
        pageBeanAct.nickName=pageName;
        pageBeanAct.startTime=intoTime;
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
        pageBeanAct.endTime=outTime;

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
        if(pageName==null){
            pageName=fragment.getClass().getSimpleName();
        }

    }
    public void onPause(Fragment fragment,String pageName) {
        if(fragment==null){
            throw new IllegalStateException("onPause() fragment不能为空");
        }
        if(pageName==null){
            pageName=fragment.getClass().getSimpleName();
        }
    }


}
