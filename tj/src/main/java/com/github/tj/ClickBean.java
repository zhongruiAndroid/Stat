package com.github.tj;

import java.io.Serializable;
import java.util.Calendar;
import java.util.UUID;

/***
 *   created by android on 2019/8/30
 */
public class ClickBean implements Serializable {


    public String _id;
    public String uid;
    public String click_id;
    public String click_name;
    public String page_name;
    public String page_nick_name;
    //后台接口需要string类型
    public String  begin_time;
    public String param_attr;
    public String page_param1;
    public String page_param2;
    public String page_param3;
    public int   data_flag=-1;
    public long  create_time;

    public ClickBean() {
        uid=UUID.randomUUID().toString();
        create_time=Calendar.getInstance().getTimeInMillis();
    }

    public void reset(){
        uid=UUID.randomUUID().toString();
        create_time=Calendar.getInstance().getTimeInMillis();

        click_id="";
        click_name="";
        page_name="";
        page_nick_name="";
        begin_time="";
        param_attr="";
        page_param1="";
        page_param2="";
        page_param3="";
        data_flag=-1;
    }
}
