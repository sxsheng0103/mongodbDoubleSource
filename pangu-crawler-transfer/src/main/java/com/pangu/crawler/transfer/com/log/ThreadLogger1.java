package com.pangu.crawler.transfer.com.log;

import java.io.IOException;

import org.apache.log4j.*;

public class ThreadLogger1
{
    ThreadLogger1() {};

    public static Logger getLogger(String logpath,String logname,Class clazz)
    {
        Logger logger = null;
        // 创建一个Logger实例, 就以线程名命名
        logger = Logger.getLogger(logname);
//        PatternLayout layout = new PatternLayout("%-4r %-5p [%d{yyyy-MM-dd HH:mm:ss,SSS}] %l%t: %m%n");
        PatternLayout layout = new PatternLayout("%-1p[%d{yyyy-MM-dd HH:mm:ss.SSS}] %l%t: %m%n");
//        layout.setConversionPattern("[%p]%d{yyyy-MM-dd HH:mm:ss,SSS} [%c]-[%M line:%L]%n %m%n");

        // 控制台输出
        ConsoleAppender concoleAppender = new ConsoleAppender(layout, "System.out");

        // 文件输出
        ThreadSeperateDailyRollingFileAppender R = null;
        try
        {
            R = new ThreadSeperateDailyRollingFileAppender(layout,logpath ,"'.'yyyy-MM-dd'.log'");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        // 参数配置, 因为没有找到仅靠配置文件的办法, 只好放在这里设
        R.setAppend(true);
        R.setImmediateFlush(true);
        R.setThreshold(Level.ALL);
        logger.removeAllAppenders();
        // 绑定到Logger
        logger.setLevel(Level.ALL);
        logger.addAppender(concoleAppender);
        logger.addAppender(R);

        logger.setAdditivity(false);//设置继承输出root
        Appender appender = null;
//        appender =new DailyRollingFileAppender(layout,"../logs/"+str1[0]+".log","yyyy-MM-dd");
        logger.addAppender(appender);
        return logger;
    }

}
