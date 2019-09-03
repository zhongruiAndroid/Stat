package com.github.tj;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;

import java.util.List;

/***
 *   created by android on 2019/8/29
 */
public class SaveHelper {
    public static boolean saveDataToSqlLite(Context context,List<PageBean> list){
        return DBManager.get(context).addData(list);
    }
    public static boolean addDataForFragment(Context context,SparseArrayCompat<PageBean> list){
        return DBManager.get(context).addDataForFragment(list);
    }
    public static boolean addData(Context context,PageBean bean){
        return DBManager.get(context).addData(bean);
    }
    public static boolean updateData(Context context,PageBean bean){
        return DBManager.get(context).updateData(bean);
    }
}
