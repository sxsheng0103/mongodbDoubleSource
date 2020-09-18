package com.pangu.crawler.transfer.utils;

import com.pangu.crawler.framework.utils.UUIDUtils;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author sheng.ding
 * @Date 2020/7/22 16:52
 * @Version 1.0
 **/
public class TempUserInfo {

    public static String activeConfigtype  = "dev";
    public static final  String user ="chinab2b";
    public static final  String pass ="tax@commonQWE";
    public static String cookieValue = UUIDUtils.get16UUID();
    public static final  long maxAge = 3600000*12l;
    private static String currentVersion = "";
    private static SimpleDateFormat sjc=new SimpleDateFormat("yyyyMMdd");

    public static synchronized  String instanceCookieValue(){
            String date = sjc.format(new Date());
            if(cookieValue!=null&&!currentVersion.equals(date)){
                currentVersion= date;
                cookieValue = cookieValue = UUIDUtils.get16UUID();
            }
        return cookieValue;
    }

}
