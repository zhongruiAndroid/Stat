package com.github.tj;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/11.
 */
public class DBConstant implements Serializable {

    public static final int pageSize = 20;
    public static final String dbName = "novel_page.db";

    public static final String T_NOVEL_PAGE = "T_NOVEL_PAGE";
    public static final String T_NOVEL_CLICK_ADVERT = "T_NOVEL_CLICK_ADVERT";

    public static final String _id="_id";//自增长，相同库相同表的唯一标识
    public static final String uid="uid";//唯一标识，不同库，相同表的唯一标识
    public static final String page_name="page_name";// 用户所在界面
    public static final String page_prev ="page_prev";// 用户上一个界面
    public static final String page_nick_name ="page_nick_name";// 用户所在界面的备注
    public static final String begin_time ="begin_time";// 开始时间，毫秒时间戳
    public static final String end_time ="end_time";// 结束时间，毫秒时间戳
    public static final String page_log_id ="page_log_id";//启动应用生成一个log_id
    public static final String page_type ="page_type";// 1:第一次启动时，2:最后退出时，3:中间 4:既是第一次启动又是最后退出,
    public static final String page_param1 ="page_param1";// 保留字段1
    public static final String page_param2 ="page_param2";// 保留字段2
    public static final String page_param3 ="page_param3";// 保留字段3

    public static final String data_flag ="data_flag";// 0删除，1没删除，debug做逻辑删除，不做物理删除，方便测试，release做物理删除
    public static final String create_time ="createTime";// 数据创建时间

    /**************************页面统计建表sql************************************/
    public static final String CT_NOVEL_PAGE_SQL = "create table "+T_NOVEL_PAGE+" (" +
            _id+" INTEGER PRIMARY KEY AUTOINCREMENT," +
            uid+" varchar," +
            page_name+" varchar," +
            page_prev+" varchar," +
            page_nick_name+" varchar," +
            begin_time+" varchar," +
            end_time+" varchar," +
            page_log_id +" varchar," +
            page_type+" varchar DEFAULT 3,"+
            page_param1+" varchar," +
            page_param2+" varchar," +
            page_param3+"  varchar," +
            data_flag+"  varchar DEFAULT 1," +
            create_time +"  varchar NOT NULL )";



    public static final String click_id ="click_id";// 事件id
    public static final String click_name ="click_name";// 事件名字
    public static final String param_attr ="param_attr";// 参数json

    /**************************广告事件和普通事件点击统计建表sql************************************/
    public static final String CT_NOVEL_CLICK_ADVERT_SQL = "create table "+T_NOVEL_CLICK_ADVERT+" (" +
            _id+" INTEGER PRIMARY KEY AUTOINCREMENT," +
            uid+" varchar," +
            click_id+" varchar," +
            click_name+" varchar," +
            page_name+" varchar," +
            page_nick_name+" varchar," +
            begin_time+" varchar," +
            param_attr+" varchar," +
            page_param1+" varchar," +
            page_param2+" varchar," +
            page_param3+"  varchar," +
            data_flag+"  varchar DEFAULT 1," +
            create_time +"  varchar NOT NULL )";
}
