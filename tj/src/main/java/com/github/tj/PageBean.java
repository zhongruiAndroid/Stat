package com.github.tj;

import java.io.Serializable;
import java.util.Calendar;
import java.util.UUID;

/***
 *   created by android on 2019/8/30
 */
public class PageBean implements Serializable {
    public String _id;
    public String uid;
    public String page_name;
    public String page_prev;
    public String page_nick_name;
    public long  begin_time;
    public long  end_time;
    public String log_id;
    // 1:第一次启动时，2:最后退出时,0:默认
    public int    page_type=-1;
    public String page_param1;
    public String page_param2;
    public String page_param3;
    public int   data_flag=-1;
    public long  create_time;

    public PageBean() {
        uid=UUID.randomUUID().toString();
        create_time=Calendar.getInstance().getTimeInMillis();
    }

    public void reset(){
        uid=UUID.randomUUID().toString();
        create_time=Calendar.getInstance().getTimeInMillis();

        page_name="";
        page_prev="";
        page_nick_name="";
        begin_time=-1;
        end_time=-1;
        log_id="";
        page_type=-1;
        page_param1="";
        page_param2="";
        page_param3="";
        data_flag=-1;
    }
}
