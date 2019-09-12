package com.github.tj;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

/***
 *   created by android on 2019/8/29
 */
public class SaveHelper implements Serializable {
    /***
     * 添加页面统计数据
     * @param context
     * @param bean
     * @return
     */
    public static boolean addData(Context context,PageBean bean){
        TJStatCore.get().upAddDataCount(context);
        return DBManager.get(context).addData(bean);
    }

    /**
     * 删除页面统计数据
     * @param context
     * @param list
     */
    public static void deleteData(Context context,List<PageBean>list){
        DBManager.get(context).deleteData(list);
    }

    /**
     * 删除某个logId下页面统计数据
     * @param context
     * @param logId
     */
    public static void deleteDataForLogId(Context context,String logId){
        DBManager.get(context).deleteDataForLogId(logId);
    }

    /**
     * 修改某个页面数据
     * @param context
     * @param bean
     * @return
     */
    public static boolean updateData(Context context,PageBean bean){
        TJStatCore.get().upAddDataCount(context);
        return DBManager.get(context).updateData(bean);
    }

    /**
     * 获取未上报的页面统计数据
     * @param context
     * @return
     */
    public static List<PageBean> getData(Context context){
        return DBManager.get(context).getData();
    }

    /**
     * 添加广告点击事件数据
     * @param context
     * @param bean
     * @return
     */
    public static boolean addAdvertClickData(Context context, ClickBean bean){
        return DBManager.get(context).addAdvertClickData(bean);
    }

    /**
     * 获取未上报的广告点击事件数据
     * @param context
     * @return
     */
    public static List<AdvertUploadBean> getAdvertClickData(Context context ){
        return DBManager.get(context).getAdvertClickData();
    }

    /**
     * 删除广告点击事件数据
     * @param context
     * @param list
     */
    public static void deleteAdvertClickData(Context context,List<AdvertUploadBean>list){
        DBManager.get(context).deleteAdvertClickData(list);
    }
    /************************************************************************************************/
    /**
     * 添加其他点击事件数据
     * @param context
     * @param bean
     * @return
     */
    public static boolean addOtherClickData(Context context, ClickBean bean){
        return DBManager.get(context).addOtherClickData(bean);
    }

    /**
     * 获取未上报的其他点击事件数据
     * @param context
     * @return
     */
    public static List<AdvertUploadBean> getOtherClickData(Context context ){
        return DBManager.get(context).getOtherClickData();
    }

    /**
     * 删除其他点击事件数据
     * @param context
     * @param list
     */
    public static void deleteOtherClickData(Context context,List<AdvertUploadBean>list){
        DBManager.get(context).deleteOtherClickData(list);
    }
}
