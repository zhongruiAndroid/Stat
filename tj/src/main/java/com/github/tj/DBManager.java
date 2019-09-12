package com.github.tj;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.SparseArrayCompat;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DBManager extends SQLiteOpenHelper implements Serializable {
    private DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private static DBManager singleObj;

    public static DBManager get(Context context) {
        if (singleObj == null) {
            synchronized (DBManager.class) {
                if (singleObj == null) {
                    singleObj = new DBManager(context, DBConstant.dbName, null, version);
                }
            }
        }
        return singleObj;
    }

    public String getDBName() {
        return DBConstant.dbName;
    }

    private static final int version = 1;

    private String getLimitSql(int page) {
        //小于等于0查询所有数据
        if (page <= 0) {
            return "";
        }
        String limit = String.format(" limit " + DBConstant.pageSize + " offset %d ", DBConstant.pageSize * (page - 1));
        return limit;
    }

    private String getLimit(int page) {
        //小于等于0查询所有数据
        if (page <= 0) {
            return null;
        }
        return DBConstant.pageSize * (page - 1) + "," + DBConstant.pageSize;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        addDataTable(db);
    }
    private void addDataTable(SQLiteDatabase db) {
        if (noExistTable(db, DBConstant.T_NOVEL_PAGE)) {
            db.execSQL(DBConstant.CT_NOVEL_PAGE_SQL);
        }
        if (noExistTable(db, DBConstant.T_NOVEL_CLICK_ADVERT)) {
            db.execSQL(DBConstant.CT_NOVEL_CLICK_ADVERT_SQL);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<newVersion){
            dropTable(db,DBConstant.T_NOVEL_PAGE);
            dropTable(db,DBConstant.T_NOVEL_CLICK_ADVERT);
            addDataTable(db);
        }
    }

    private boolean dropTable(SQLiteDatabase db, String table) {
        try {
            db.execSQL("drop table " + table);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean existTable(SQLiteDatabase db, String table) {
        boolean exits = false;
        String sql = "select * from sqlite_master where name=?";
        Cursor cursor = db.rawQuery(sql, new String[]{table});
        if (cursor.getCount() != 0) {
            exits = true;
        }
        cursor.close();
        return exits;
    }

    private boolean noExistTable(SQLiteDatabase db, String table) {
        boolean exits = true;
        String sql = "select * from sqlite_master where name=?";
        Cursor cursor = db.rawQuery(sql, new String[]{table});
        if (cursor.getCount() != 0) {
            exits = false;
        }
        cursor.close();
        return exits;
    }

    public boolean addDataForFragment(SparseArrayCompat<PageBean> list) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            for (int i = 0; i < list.size(); i++) {
                PageBean bean = list.valueAt(i);
                insertData(db, bean);
            }
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeDB(db);
        }
        return true;
    }

    public boolean addData(List<PageBean> list) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            for (PageBean bean : list) {
                insertData(db, bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeDB(db);
        }
        return true;
    }

    public boolean addData(PageBean bean) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            insertData(db, bean);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeDB(db);
        }
        return true;
    }

    private void insertData(SQLiteDatabase db, PageBean bean) {
        ContentValues values = new ContentValues();
        values.put(DBConstant.uid, bean.uid);
        values.put(DBConstant.page_name, bean.page_name);
        if (TextUtils.isEmpty(bean.page_prev)) {
            bean.page_prev = PageBean.APP_LAUNCH;
        }
        values.put(DBConstant.page_prev, bean.page_prev);
        values.put(DBConstant.page_nick_name, bean.page_nick_name);
        values.put(DBConstant.begin_time, bean.begin_time);
        values.put(DBConstant.end_time, bean.end_time);
        values.put(DBConstant.page_log_id, bean.page_log_id);
        if (TextUtils.isEmpty(bean.page_type) == false) {
            values.put(DBConstant.page_type, bean.page_type);
        }
        values.put(DBConstant.page_param1, bean.page_param1);
        values.put(DBConstant.page_param2, bean.page_param2);
        values.put(DBConstant.page_param3, bean.page_param3);
        if (bean.data_flag != -1) {
            values.put(DBConstant.data_flag, bean.data_flag);
        }
        values.put(DBConstant.create_time, bean.create_time);
        db.insert(DBConstant.T_NOVEL_PAGE, null, values);
    }

    public boolean updateData(PageBean bean) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBConstant.uid, bean.uid);
            values.put(DBConstant.page_name, bean.page_name);
            if (TextUtils.isEmpty(bean.page_prev)) {
                bean.page_prev = PageBean.APP_LAUNCH;
            }
            values.put(DBConstant.page_prev, bean.page_prev);
            values.put(DBConstant.page_nick_name, bean.page_nick_name);
            values.put(DBConstant.begin_time, bean.begin_time);
            values.put(DBConstant.end_time, bean.end_time);
            values.put(DBConstant.page_log_id, bean.page_log_id);
            if (TextUtils.isEmpty(bean.page_type) == false) {
                values.put(DBConstant.page_type, bean.page_type);
            }
            values.put(DBConstant.page_param1, bean.page_param1);
            values.put(DBConstant.page_param2, bean.page_param2);
            values.put(DBConstant.page_param3, bean.page_param3);
            values.put(DBConstant.data_flag, "1");
            values.put(DBConstant.create_time, bean.create_time);
            int update = db.update(DBConstant.T_NOVEL_PAGE, values, DBConstant.uid + " = ? ", new String[]{bean.uid});
            return update > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeDB(db);
        }
    }

    private void closeDB(SQLiteDatabase db) {
        if (db != null) {
            db.close();
        }
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    public List<PageBean> getData() {
        List<PageBean> list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getReadableDatabase();

            cursor = db.query(DBConstant.T_NOVEL_PAGE,
                    new String[]{
                            DBConstant.uid,
                            DBConstant.page_name,
                            DBConstant.page_prev,
                            DBConstant.page_nick_name,
                            DBConstant.begin_time,
                            DBConstant.end_time,
                            DBConstant.page_log_id,
                            DBConstant.page_type},
                    DBConstant.data_flag + " = ? ", new String[]{"1"}, null, null, null);
            while (cursor.moveToNext()) {
                PageBean bean = new PageBean();
                bean.uid = cursor.getString(cursor.getColumnIndex(DBConstant.uid));

                bean.page_name = cursor.getString(cursor.getColumnIndex(DBConstant.page_name));
                bean.page_prev = cursor.getString(cursor.getColumnIndex(DBConstant.page_prev));
                bean.page_nick_name = cursor.getString(cursor.getColumnIndex(DBConstant.page_nick_name));
                bean.begin_time = cursor.getString(cursor.getColumnIndex(DBConstant.begin_time));
                bean.end_time = cursor.getString(cursor.getColumnIndex(DBConstant.end_time));
                bean.page_log_id = cursor.getString(cursor.getColumnIndex(DBConstant.page_log_id));
                bean.page_type = cursor.getString(cursor.getColumnIndex(DBConstant.page_type));

                list.add(bean);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            closeCursor(cursor);
            closeDB(db);
        }
    }


    public void deleteData(List<PageBean> list) {
        if (list == null) {
            return;
        }

        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            for (int i = 0; i < list.size(); i++) {
                PageBean pageBean = list.get(i);
                if (TJStatCore.get().isDebug()) {
                    //debug模式逻辑删除,便于测试
                    ContentValues values = new ContentValues();
                    values.put(DBConstant.data_flag, "0");
                    db.update(DBConstant.T_NOVEL_PAGE, values, DBConstant.uid + "=?", new String[]{pageBean.uid});
                } else {
                    //release模式物理删除,防止数据过多
                    db.delete(DBConstant.T_NOVEL_PAGE, DBConstant.uid + "=?", new String[]{pageBean.uid});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDB(db);
        }
    }

    public void deleteDataForLogId(String logId) {
        if (TextUtils.isEmpty(logId)) {
            return;
        }
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.delete(DBConstant.T_NOVEL_PAGE, DBConstant.page_log_id + "=?", new String[]{logId});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDB(db);
        }
    }

    public boolean addAdvertClickData(ClickBean bean) {
        bean.event_type = "0";
        return addEventClickData(bean);
    }

    public boolean addOtherClickData(ClickBean bean) {
        bean.event_type = "1";
        return addEventClickData(bean);
    }

    public boolean addEventClickData(ClickBean bean) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBConstant.uid, bean.uid);
            values.put(DBConstant.click_id, bean.click_id);
            values.put(DBConstant.click_name, bean.click_name);
            values.put(DBConstant.page_name, bean.page_name);
            values.put(DBConstant.page_nick_name, bean.page_nick_name);
            values.put(DBConstant.begin_time, bean.begin_time);
            values.put(DBConstant.param_attr, bean.param_attr);
            values.put(DBConstant.page_param1, bean.page_param1);
            values.put(DBConstant.page_param2, bean.page_param2);
            values.put(DBConstant.page_param3, bean.page_param3);
            if (bean.data_flag != -1) {
                values.put(DBConstant.data_flag, bean.data_flag);
            }
            values.put(DBConstant.event_type, bean.event_type);
            values.put(DBConstant.create_time, bean.create_time);
            db.insert(DBConstant.T_NOVEL_CLICK_ADVERT, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeDB(db);
        }
        return true;
    }

    //广告事件
    public List<AdvertUploadBean> getAdvertClickData() {
        return getEventClickData("0");
    }

    //其他事件
    public List<AdvertUploadBean> getOtherClickData() {
        return getEventClickData("1");
    }

    public List<AdvertUploadBean> getEventClickData(String eventType) {
        List<AdvertUploadBean> list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getReadableDatabase();

            cursor = db.query(DBConstant.T_NOVEL_CLICK_ADVERT,
                    new String[]{
                            DBConstant.uid,
                            DBConstant.click_id,
                            DBConstant.click_name,
                            DBConstant.page_nick_name,
                            DBConstant.begin_time,
                            DBConstant.param_attr},
                    DBConstant.data_flag + " = ? and " + DBConstant.event_type + " = ? ", new String[]{"1", eventType}, null, null, null);
            while (cursor.moveToNext()) {
                AdvertUploadBean bean = new AdvertUploadBean();
                bean.uid = cursor.getString(cursor.getColumnIndex(DBConstant.uid));
                //接口需要的advert_name对应click_id
                //接口需要的advert_nick_name对应click_name
                //接口需要的advert_page_name对应page_nick_name
                bean.advert_name = cursor.getString(cursor.getColumnIndex(DBConstant.click_id));
                bean.advert_nick_name = cursor.getString(cursor.getColumnIndex(DBConstant.click_name));
                bean.advert_page_name = cursor.getString(cursor.getColumnIndex(DBConstant.page_nick_name));
                bean.begin_time = cursor.getString(cursor.getColumnIndex(DBConstant.begin_time));
                String paramJson = cursor.getString(cursor.getColumnIndex(DBConstant.param_attr));

                ParamAttr paramAttr = getParamAttr(paramJson);
                bean.book_id = paramAttr.book_id;
                bean.chapter_id = paramAttr.chapter_id;

                list.add(bean);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            closeCursor(cursor);
            closeDB(db);
        }
    }

    private ParamAttr getParamAttr(String json) {
        ParamAttr paramAttr = new ParamAttr();
        if (TextUtils.isEmpty(json)) {
            return paramAttr;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            Class<?> aClass = paramAttr.getClass();
            boolean flag = true;
            while (flag) {
                Field[] declaredFields = aClass.getDeclaredFields();
                if (declaredFields == null) {
                    return paramAttr;
                }
                int length = declaredFields.length;
                if (length == 0) {
                    return paramAttr;
                }
                for (int i = 0; i < length; i++) {
                    declaredFields[i].setAccessible(true);
                    String name = declaredFields[i].getName();
                    try {
                        declaredFields[i].set(paramAttr, jsonObject.getString(name) == null ? "" : jsonObject.getString(name));
                    } catch (Exception e) {
                    }
                }
                Class<?> superclass = aClass.getSuperclass();
                String name = superclass.getName();
                if (name.startsWith("java.") ||
                        name.startsWith("javax.") ||
                        name.startsWith("android.") ||
                        name.startsWith("androidx.")
                        ) {
                    flag = false;
                } else {
                    aClass = superclass;
                }
            }
//这里用反射获取属性名字用于json解析，这样每增加或修改一个属性 都不需要增加jsonObject.getString("属性名")代码了
//            paramAttr.book_id=jsonObject.getString("book_id");
//            paramAttr.chapter_id=jsonObject.getString("chapter_id");
        } catch (Exception e) {
        }
        return paramAttr;
    }

    public void deleteAdvertClickData(List<AdvertUploadBean> list) {
        deleteEventClickData(list, "0");
    }

    public void deleteOtherClickData(List<AdvertUploadBean> list) {
        deleteEventClickData(list, "1");
    }

    public void deleteEventClickData(List<AdvertUploadBean> list, String eventType) {
        if (list == null) {
            return;
        }
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            for (int i = 0; i < list.size(); i++) {
                AdvertUploadBean pageBean = list.get(i);
                if (TJStatCore.get().isDebug()) {
                    //debug模式逻辑删除,便于测试
                    ContentValues values = new ContentValues();
                    values.put(DBConstant.data_flag, "0");
                    db.update(DBConstant.T_NOVEL_CLICK_ADVERT, values, DBConstant.uid + "=? and " + DBConstant.event_type + " =? ", new String[]{pageBean.uid, eventType});
                } else {
                    //release模式物理删除,防止数据过多
                    db.delete(DBConstant.T_NOVEL_CLICK_ADVERT, DBConstant.uid + "=? and " + DBConstant.event_type + " =? ", new String[]{pageBean.uid, eventType});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDB(db);
        }
    }
}
