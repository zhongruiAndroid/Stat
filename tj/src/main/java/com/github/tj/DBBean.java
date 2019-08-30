package com.github.tj;

import java.io.Serializable;
import java.util.Calendar;
import java.util.UUID;

/***
 *   created by android on 2019/8/30
 */
public class DBBean implements Serializable {
    public String _id;
    public String uid;
    public String page_name;
    public String page_prev;
    public String page_nick_name;
    public String begin_time;
    public String end_time;
    public String log_id;
    public int page_type=-1;
    public String page_param1;
    public String page_param2;
    public String page_param3;
    public int data_flag=-1;
    public long create_time;

    public DBBean() {
        uid=UUID.randomUUID().toString();
        create_time=Calendar.getInstance().getTimeInMillis();
    }
}
