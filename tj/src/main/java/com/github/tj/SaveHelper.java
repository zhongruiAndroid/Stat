package com.github.tj;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

/***
 *   created by android on 2019/8/29
 */
public class SaveHelper implements Serializable {
    public static boolean addData(Context context,PageBean bean){
        TJStatCore.get().upAddDataCount(context);
        return DBManager.get(context).addData(bean);
    }
    public static void deleteData(Context context,List<PageBean>list){
        DBManager.get(context).deleteData(list);
    }
    public static boolean updateData(Context context,PageBean bean){
        TJStatCore.get().upAddDataCount(context);
        return DBManager.get(context).updateData(bean);
    }
    public static List<PageBean> getData(Context context){
        return DBManager.get(context).getData();
    }

    //添加广告点击事件
    public static boolean addAdvertClickData(Context context, ClickBean bean){
        return DBManager.get(context).addAdvertClickData(bean);
    }

    //获取广告点击事件数据
    public static List<AdvertUploadBean> getAdvertClickData(Context context ){
        return DBManager.get(context).getAdvertClickData();
    }
    //删除广告点击事件
    public static void deleteAdvertClickData(Context context,List<AdvertUploadBean>list){
        DBManager.get(context).deleteAdvertClickData(list);
    }
}
