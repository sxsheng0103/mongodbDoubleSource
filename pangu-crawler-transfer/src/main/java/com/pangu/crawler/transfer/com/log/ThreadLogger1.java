package com.pangu.crawler.transfer.com.log;

import org.apache.log4j.*;

import java.io.IOException;

public class ThreadLogger1
{
    ThreadLogger1() {};

    public static Logger getLogger(String logpath,String logname,Class clazz)
    {
        Logger logger = null;
        // 创建一个Logger实例, 就以线程名命名
        logger = Logger.getLogger(logname);
        /*
        %c 输出日志信息所属的类的全名
        %d 输出日志时间点的日期或时间，默认格式为ISO8601，也可以在其后指定格式，比如：%d{yyy-MM-dd HH:mm:ss }，输出类似：2002-10-18- 22：10：28
        %f 输出日志信息所属的类的类名
        %l 输出日志事件的发生位置，即输出日志信息的语句处于它所在的类的第几行
        %m 输出代码中指定的信息，如log(message)中的message
        %n 输出一个回车换行符，Windows平台为“rn”，Unix平台为“n”
        %p 输出优先级，即DEBUG，INFO，WARN，ERROR，FATAL。如果是调用debug()输出的，则为DEBUG，依此类推
        %r 输出自应用启动到输出该日志信息所耗费的毫秒数
        %t 输出产生该日志事件的线程名
        */
//        PatternLayout layout = new PatternLayout("%-4r %-5p [%d{yyyy-MM-dd HH:mm:ss,SSS}] %l%t: %m%n");
        PatternLayout layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss.SSS} %-4p %l %m%n");
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
