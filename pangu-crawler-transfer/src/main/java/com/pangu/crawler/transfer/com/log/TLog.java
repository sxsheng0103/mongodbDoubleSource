package com.pangu.crawler.transfer.com.log;

import com.pangu.crawler.framework.utils.StringUtils;
import com.pangu.crawler.transfer.service.TransferRuleService;
import com.pangu.crawler.transfer.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.springframework.boot.system.ApplicationHome;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
@Slf4j
public class TLog {
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
    public static Logger registerLog(Logger logger,Class clazz){
        logger =  instancelog(true,clazz);
        return logger;
    }

    public static Logger instancelog(boolean datesplit,Class clazz){
        Logger logger = null;
        try{
            String logname = "";
            if(datesplit!=true){
                currentDateS = "";
            }else if(!currentDateS.equals(TimeUtils.getCurrentDateTime(null,TimeUtils.sdf6))){
                currentDateS = TimeUtils.getCurrentDateTime(null,TimeUtils.sdf6);
            }
            logname = currentDateS;
            if(CacheInfo.threadglobalName.get()==null){
                return null;
            }
            File logfile = new File(logpath+"\\Tlog\\"+CacheInfo.threadglobalName.get().getTaskname());
            if(!logfile.isDirectory()){
                logfile.mkdirs();
            }
            if(loggerMaps.get(CacheInfo.threadglobalName.get().getTaskid())!=null){
                logger = loggerMaps.get(CacheInfo.threadglobalName.get().getTaskid());
                loggerMaps.put(CacheInfo.threadglobalName.get().getTaskid(),logger);
            }
            if(loggerMaps.get(CacheInfo.threadglobalName.get().getTaskid())==null){
                logger = ThreadLogger1.getLogger(logpath+"\\Tlog\\"+CacheInfo.threadglobalName.get().getTaskname()+"\\tranfser-"+logname+".log",CacheInfo.threadglobalName.get().getTaskid(),clazz);
                loggerMaps.put(CacheInfo.threadglobalName.get().getTaskid(),logger);
            }

//        LOGGER.addHandler(new ConsoleHandler());//输出到控制台
        }catch (Exception e){
            log.error("日志系统出错"+e.getMessage());
            e.printStackTrace();
        }finally{
            return logger;
        }
    }

}

