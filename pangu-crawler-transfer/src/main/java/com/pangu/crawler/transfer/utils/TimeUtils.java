package com.pangu.crawler.transfer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author sheng.ding
 * @Date 2020/7/26 17:37
 * @Version 1.0
 **/
public class TimeUtils {
    public static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd hhmmss");
    public static final SimpleDateFormat sdf4 = new SimpleDateFormat("yyyyMMddHHmm");
    public static final SimpleDateFormat sdf5 = new SimpleDateFormat("HH:mm:ss");
    public static final SimpleDateFormat sdf6 = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat sdfym = new SimpleDateFormat("yyyyMM");
    public static final SimpleDateFormat sdfy = new SimpleDateFormat("yyyy");
    public static final SimpleDateFormat sdfm = new SimpleDateFormat("MM");


    public static String getCurrentDateTime(Date date, SimpleDateFormat sdf){
        if(date == null){
            date = new Date();
        }
        return  sdf.format(date);
    }
}
