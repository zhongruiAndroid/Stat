package com.github.tj;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.SparseArrayCompat;


import java.io.Serializable;
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

    private void addDataTable(SQLiteDatabase db, String table) {
        if (noExistTable(db, table)) {
            db.execSQL(table);
        }
    }

    private void addDataTable(SQLiteDatabase db) {
        if (noExistTable(db, DBConstant.T_NOVEL_PAGE)) {
            db.execSQL(DBConstant.CT_NOVEL_PAGE_SQL);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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
        SQLiteDatabase db = getWritableDatabase();
        if(db.isOpen()==false){
            return false;
        }
        try {
            db.beginTransaction();
            for (int i = 0; i < list.size(); i++) {
                PageBean bean=list.valueAt(i);
                insertData(db, bean);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
        return true;
    }
    public boolean addData(List<PageBean> list) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            for (PageBean bean : list) {
                insertData(db, bean);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
        return true;
    }
    private void insertData(SQLiteDatabase db, PageBean bean) {
        ContentValues values = new ContentValues();
        values.put(DBConstant.uid, bean.uid);
        values.put(DBConstant.page_name, bean.page_name);
        values.put(DBConstant.page_prev, bean.page_prev);
        values.put(DBConstant.page_nick_name, bean.page_nick_name);
        values.put(DBConstant.begin_time, bean.begin_time);
        values.put(DBConstant.end_time, bean.end_time);
        values.put(DBConstant.log_id, bean.log_id);
        if (bean.page_type != -1) {
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


    public long addData(PageBean bean) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBConstant.uid, bean.uid);
        values.put(DBConstant.page_name, bean.page_name);
        values.put(DBConstant.page_prev, bean.page_prev);
        values.put(DBConstant.page_nick_name, bean.page_nick_name);
        values.put(DBConstant.begin_time, bean.begin_time);
        values.put(DBConstant.end_time, bean.end_time);
        values.put(DBConstant.log_id, bean.log_id);
        if (bean.page_type != -1) {
            values.put(DBConstant.page_type, bean.page_type);
        }
        values.put(DBConstant.page_param1, bean.page_param1);
        values.put(DBConstant.page_param2, bean.page_param2);
        values.put(DBConstant.page_param3, bean.page_param3);
        if (bean.data_flag != -1) {
            values.put(DBConstant.data_flag, bean.data_flag);
        }
        values.put(DBConstant.create_time, bean.create_time);
        long insert = db.insert(DBConstant.T_NOVEL_PAGE, null, values);
        db.close();
        return insert;
    }

    /*public long updateMemo(MemoBean bean) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBConstant.dataRemark,bean.getDataRemark());
        values.put(DBConstant.dataContent,bean.getDataContent());
        values.put(DBConstant.updateTime, DateUtils.getLocalDate());
        long insert = db.update(T_Memo_Note, values, DBConstant._id + "=?", new String[]{bean.get_id() + ""});
        db.close();
        return insert;
    }*/

}
