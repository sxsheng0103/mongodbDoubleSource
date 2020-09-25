package com.pangu.crawler.transfer.com.log;

import com.pangu.crawler.framework.utils.StringUtils;
import com.pangu.crawler.transfer.service.TransferRuleService;
import com.pangu.crawler.transfer.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.juli.OneLineFormatter;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.*;
@Slf4j
public class TLog {
    private static Logger LOGGER = null;
    private static final Properties properties = new Properties();
    private static  String currentDateS = TimeUtils.getCurrentDateTime(null,TimeUtils.sdf6);
    private static Map<String,FileHandler> fileHandlerMap = new HashMap<String,FileHandler>();
    private static String logpath = "";

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
     /*   try{
            if(StringUtils.isNotEmpty(logpath)){
                fileHandler=new FileHandler(logpath+"\\"+TimeUtils.getCurrentDateTime(null,TimeUtils.sdf1)+".log",true);//追加方式输出日志
            }
        }catch (IOException e){
            log.error("多线程转化任务初始化日志输出路径获取错误!"+e.getMessage());
        }*/
       /* try{
            if(StringUtils.isEmpty(logpath)){
                logpath = new ApplicationHome(TLog.class).getSource().getAbsolutePath()+"\\..\\log\\";
                fileHandler=new FileHandler("C:\\Log\\JDKLog1.log",true);//追加方式输出日志
            }
        }catch (IOException e){
            log.error("多线程转化任务初始化日志默认输出路径获取错误!"+e.getMessage());
        }*/

    }


    public  TLog(Class clazz){
        LOGGER =  Logger.getLogger(clazz.toString());

    }

    public FileHandler instancelog(boolean datesplit){
        FileHandler fhandler = null;
        try{
            String logname = "";
            if(datesplit!=true){
                currentDateS = "";
            }else if(!currentDateS.equals(TimeUtils.getCurrentDateTime(null,TimeUtils.sdf6))){
                currentDateS = TimeUtils.getCurrentDateTime(null,TimeUtils.sdf6);
            }
            logname = "tranfser-"+currentDateS;
            File logfile = new File(logpath+"\\Tlog\\"+CacheInfo.threadglobalName.get().getTaskname());
            if(!logfile.isDirectory()){
                logfile.mkdirs();
            }
            logfile = new File(logpath+"\\Tlog\\"+CacheInfo.threadglobalName.get().getTaskname()+"\\\\"+logname+".log");
            if(!logfile.isFile()){
                logfile.createNewFile();
                fhandler =new FileHandler(logpath+"\\Tlog\\"+CacheInfo.threadglobalName.get().getTaskname()+"\\\\"+logname+".log",true);
                fhandler.setFormatter(new OneLineFormatter());//输出格式
//                    LOGGER.addHandler(fhandler);
                LOGGER.setLevel(Level.ALL);//输出级别
                fileHandlerMap.put(CacheInfo.threadglobalName.get().getTaskid(),fhandler);//追加方式输出日志
            }else{
                if(fileHandlerMap.get(CacheInfo.threadglobalName.get().getTaskid())==null){
                    fhandler =new FileHandler(logpath+"\\Tlog\\"+CacheInfo.threadglobalName.get().getTaskname()+"\\\\"+logname+".log",true);
                    fhandler.setFormatter(new OneLineFormatter());//输出格式
//                    LOGGER.addHandler(fhandler);
                    LOGGER.setLevel(Level.ALL);//输出级别
                    fileHandlerMap.put(CacheInfo.threadglobalName.get().getTaskid(),fhandler);//追加方式输出日志
                }else{
                    fhandler = fileHandlerMap.get(CacheInfo.threadglobalName.get().getTaskid());
                }
//                LOGGER.addHandler(fhandler);
            }
//        LOGGER.addHandler(new ConsoleHandler());//输出到控制台
        }catch (Exception e){
            log.error("日志系统出错"+e.getMessage());
            e.printStackTrace();
        }finally{
            return fhandler;
        }
    }

    /**
     * @param msg 任务名称 一个线程任务名对应一个
     * @param default1
     * @param level  输出级别
     * @param inconsole 是否在控制台输出
     * @param loggormatter  日志输出模板
     */
    public  void printTLog(String msg,String default1,Level level,boolean inconsole,Formatter loggormatter,boolean datesplit){
        FileHandler fhandler = instancelog(true);
        LOGGER.addHandler(fhandler);
        if(level.equals(Level.INFO)){
            LOGGER.info(msg);
        }else if(level.equals(Level.WARNING)){
            LOGGER.warning(msg);
        }else if(level.equals(Level.FINE)){
            LOGGER.fine(msg);
        }else{
            LOGGER.fine(msg);
        }
        fhandler.close();
    }


    /**
     * @param msg 线程名称
     */
    public void info(String msg){
        printTLog(msg,null,Level.INFO,true,null,false);
    }

    /**
     * @param msg 线程名称
     */
    public void warn(String msg){
        printTLog(msg,null,Level.WARNING,true,null,false);
    }

    /**
     * @param msg 线程名称
     */
    public void error(String msg){
        printTLog(msg,null,Level.FINE,true,null,false);
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
        LOGGER =  Logger.getLogger("");
        logfile = new File(logpath+"\\Tlog\\"+CacheInfo.threadglobalName.get().getTaskname()+"\\\\"+currentDateS+".log");
        if(!logfile.isFile()){
            logfile.createNewFile();
        }
        FileHandler fileHandler=new FileHandler(logpath+"\\Tlog\\"+CacheInfo.threadglobalName.get().getTaskname()+"\\\\"+currentDateS+".log",true);//追加方式输出日志
        fileHandler.setFormatter(new OneLineFormatter());//输出格式
        LOGGER.addHandler(fileHandler);
        new CacheInfo("we",null,null,null);
        String a = "";

    }
}

