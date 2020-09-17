package com.pangu.crawler.transfer.globalevent;

import java.io.File;

/**
 * @Author sheng.ding
 * @Date 2020/8/5 17:05
 * @Version 1.0
 **/
public class TimerLogUtils {
    /**
     *
     */

    public static void dynamicLogFileStrategy(String sheduleName,String defaultString,long timeperiod){
        String logpath = TimerLogUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File logdir = new File(logpath);
        if(logdir.exists()&&logdir.isDirectory()){
        }else{
            logdir.mkdirs();
        }
        File logfile = new File(logpath+"\\"+sheduleName+".log");
        if(logfile.isFile()){

        }
    }
}
