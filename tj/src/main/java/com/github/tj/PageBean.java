package com.github.tj;

import java.io.Serializable;
import java.util.Calendar;
import java.util.UUID;

/***
 *   created by android on 2019/8/30
 */
public class PageBean implements Serializable {
    //用来判断退出app时，最后一个fragment是否在最后一个activity里面
    public String actName="";
    //第一次启动时
    public static final String PAGE_TYPE_INTO="1";
    //最后退出时
    public static final String PAGE_TYPE_OUT="2";
    //既是第一次启动又是最后退出,
    public static final String PAGE_TYPE_INTO_OUT="4";
    //中间跳转
    public static final String PAGE_TYPE_OTHER="3";
    //如果首次进入，没有上个页面，保存数据到数据库的时候page_prev就设置为"启动了"
    public static final String APP_LAUNCH="启动了";

    public String _id;
    public String uid;
    public String page_name;
    public String page_prev;
    public String page_nick_name="";
    //后台接口需要string类型
    public String  begin_time;
    public String  end_time;
    public String page_log_id;
    // 1:第一次启动时，2:最后退出时，3:中间 4:既是第一次启动又是最后退出,
    public String    page_type="";
    public String page_param1;
    public String page_param2;
    public String page_param3;
    public int   data_flag=-1;
    public long  create_time;

    public PageBean() {
        uid=UUID.randomUUID().toString();
        create_time=Calendar.getInstance().getTimeInMillis();

        actName="";
        page_name="";
        page_prev="";
        page_nick_name="";
        begin_time="";
        end_time="";
        page_log_id ="";
        page_type="";
        page_param1="";
        page_param2="";
        page_param3="";
        data_flag=-1;
    }


}
