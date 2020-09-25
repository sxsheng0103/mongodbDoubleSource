package com.pangu.crawler.transfer.com.log;

import com.pangu.crawler.framework.utils.StringUtils;
import com.pangu.crawler.transfer.service.TransferRuleService;
import com.pangu.crawler.transfer.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Level;

import org.apache.logging.log4j.Logger;
import org.springframework.boot.system.ApplicationHome;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
@Slf4j
public class TLog {
    private Logger LOGGER = null;
    private static final Properties properties = new Properties();
    private static  String currentDateS = TimeUtils.getCurrentDateTime(null,TimeUtils.sdf6);
    private static String logpath = "";

    private static Map<String,Logger> loggerMaps = new HashMap<String,Logger>();

    static {
        try{
            properties.load(TransferRuleService.class.getResourceAsStream("/config.properties"));
            logpath = properties.get("logfile")==null?"":properties.get("logfile").toString().trim();
            if(StringUtils.isEmpty(logpath)){
                logpath = new ApplicationHome(TLog.class).getSource().getAbsolutePath()+"\\..\\log\\";
            }
        }catch (Exception e){
            log.error("加载初始化参数失败");
        }
    }
    public static TLog registerLog(Class clazz){
        return new TLog(instancelog(true));
    }

    public TLog(Logger LOGGER){
        this.LOGGER = LOGGER;
    }

    public  TLog(Class clazz){
        LOGGER = instancelog(true);
    }

    public static Logger instancelog(boolean datesplit){
        Logger logger = null;
        try{
            String logname = "";
            if(datesplit!=true){
                currentDateS = "";
            }else if(!currentDateS.equals(TimeUtils.getCurrentDateTime(null,TimeUtils.sdf6))){
                currentDateS = TimeUtils.getCurrentDateTime(null,TimeUtils.sdf6);
            }
            logname = "tranfser-"+currentDateS;
            if(CacheInfo.threadglobalName.get()==null){
                return null;
            }
            File logfile = new File(logpath+"\\Tlog\\"+CacheInfo.threadglobalName.get().getTaskname());
            if(!logfile.isDirectory()){
                logfile.mkdirs();
            }
            logfile = new File(logpath+"\\Tlog\\"+CacheInfo.threadglobalName.get().getTaskname()+"\\"+logname+".log");
            if(!logfile.isFile()){
                logfile.createNewFile();
            }
            logger = LogUtil.getLogger(CacheInfo.threadglobalName.get().getTaskid());
       /*     if(loggerMaps.get(CacheInfo.threadglobalName.get().getTaskid())!=null){
                logger = loggerMaps.get(CacheInfo.threadglobalName.get().getTaskid());
                logger.removeAllAppenders();
                logger = ThreadLogger.getLogger(logpath+"\\Tlog\\"+CacheInfo.threadglobalName.get().getTaskname()+"\\",logname);
                loggerMaps.put(CacheInfo.threadglobalName.get().getTaskid(),logger);
            }
            if(loggerMaps.get(CacheInfo.threadglobalName.get().getTaskid())==null){
                logger = ThreadLogger.getLogger(logpath+"\\Tlog\\"+CacheInfo.threadglobalName.get().getTaskname()+"\\",logname);
                loggerMaps.put(CacheInfo.threadglobalName.get().getTaskid(),logger);
            }
*/
//        LOGGER.addHandler(new ConsoleHandler());//输出到控制台
        }catch (Exception e){
            log.error("日志系统出错"+e.getMessage());
            e.printStackTrace();
        }finally{
            return logger;
        }
    }

    /**
     * @param msg 任务名称 一个线程任务名对应一个
     * @param default1
     * @param level  输出级别
     * @param inconsole 是否在控制台输出
     */
    public  void printTLog(String msg, String default1, Level level, boolean inconsole,  boolean datesplit){
//        Logger logger = instancelog(true);
        if(level.equals(Level.INFO)){
            this.LOGGER.info(msg);
        }else if(level.equals(Level.WARN)){
            this.LOGGER.warn(msg);
        }else if(level.equals(Level.ERROR)){
            this.LOGGER.error(msg);
        }else if(level.equals(Level.DEBUG)){
            this.LOGGER.debug(msg);
        }else{
            this.LOGGER.info(msg);
        }
    }

    /**
     * @param msg 线程名称
     */
    public void info(String msg){
        printTLog(msg,null,Level.INFO,true,false);
    }

    /**
     * @param msg 线程名称
     */
    public void warn(String msg){
        printTLog(msg,null,Level.WARN,true,false);
    }

    /**
     * @param msg 线程名称
     */
    public void error(String msg){
        printTLog(msg,null,Level.ERROR,true,false);
    }
    /**
     * @param msg 线程名称
     */
    public void debug(String msg){
        printTLog(msg,null,Level.DEBUG,true,false);
    }

    public static void main(String[] args) throws IOException {
        instancelog1("adsa",true);
    }

    public static void instancelog1(String msg,boolean datesplit) throws IOException {
        if(!currentDateS.equals(TimeUtils.getCurrentDateTime(null,TimeUtils.sdf6))){
            currentDateS = TimeUtils.getCurrentDateTime(null,TimeUtils.sdf6);
        }
        File logfile = new File(logpath+"\\Tlog\\"+CacheInfo.threadglobalName.get().getTaskname());
        if(!logfile.isDirectory()){
            logfile.mkdirs();
        }
//        LOGGER =  Logger.getLogger("");
        logfile = new File(logpath+"\\Tlog\\"+CacheInfo.threadglobalName.get().getTaskname()+"\\\\"+currentDateS+".log");
        if(!logfile.isFile()){
            logfile.createNewFile();
        }
        new CacheInfo("we",null,null,null);
        String a = "";

    }
}

