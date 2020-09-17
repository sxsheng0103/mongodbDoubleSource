package com.pangu.crawler.transfer.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncAreaTaxCodeReleationEntity;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncBusinessTransferRuleEntity;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncQueryHistoricalDataInfoEntity;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncTaskTimerEntity;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryStockDataOperation;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.Paging;
import com.pangu.crawler.framework.utils.Base64Util;
import com.pangu.crawler.framework.utils.StringUtils;
import com.pangu.crawler.framework.utils.UUIDUtils;
import com.pangu.crawler.transfer.enums.SchedualEnum;
import com.pangu.crawler.transfer.service.iservice.ITransferHtmlDataService;
import com.pangu.crawler.transfer.service.iservice.ITransferJsonDataService;
import com.pangu.crawler.transfer.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.rmi.server.UID;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import org.springframework.scheduling.support.CronTrigger;

@Service
@Slf4j
public class TransferRuleService {

    @Autowired
    TaskManagerService taskManagerService;

    @Autowired
    TaxCodeReleationService taxCodeReleationService;

    @Autowired
    TransferRuleDataService transferRuleDataService;

    @Autowired
    ITransferJsonDataService transferJsonDataService;

    @Autowired
    ITransferHtmlDataService transferHtmlDataService;

    @Autowired
    CrawleredDataService crawleredDataService;

    @Autowired
    AsyncQueryStockDataOperation asyncQueryStockDataOperation;

    private static final Properties properties = new Properties();
    private static  boolean noondealing = true;
    private static  List<String> originSzCodes = new ArrayList<String>();
    static{
        try{
            properties.load(TransferRuleService.class.getResourceAsStream("/config.properties"));
        }catch (Exception e){
            log.error("加载初始化参数失败");
        }
    }

    /**
     * @description 执行转化报文事件(重新执行)
     */
    public void executeTransferService(String id){
        try{
            initOriginSzs("","");
        }catch (Exception e){
            log.warn("查找税种关系对应关系失败!需尽快排查问题");
            new RuntimeException("查找税种关系对应关系失败!");
        }
        if(!originSzCodes.isEmpty()&&noondealing == true){
            TransferService(null,id,"","");
        }else{
            log.warn("没有查到符合的税种对应关系");
            new RuntimeException("没有查到符合的税种对应关系!");
        }
    }

    /**
     * @description 执行转化报文事件
     * @param szs
     * @param dqs
     * @param isdelay
     * @param delaytime
     * @param exeType
     * @param expresslist
     */
    public void executeTransferService(String szs, String dqs,String isdelay, long delaytime, SchedualEnum exeType, Object expresslist){
        log.info("地区:"+dqs+"税种:"+szs);
        try{
            initOriginSzs(dqs,szs);
        }catch (Exception e){
            log.warn("查找税种关系对应关系失败!需尽快排查问题");
            new RuntimeException("查找税种关系对应关系失败!");
        }
        if(!originSzCodes.isEmpty()&&noondealing == true){
            TransferServiceDispatch("1",null,szs,dqs);
        }else{
            log.warn("没有查到符合的税种对应关系");
            new RuntimeException("没有查到符合的税种对应关系!");
        }
    }

    /**
     * @description 初始化地区、税种原始税种代码
     * @throws Exception
     */
    private  void initOriginSzs(String dqs,String szs)throws Exception{
        Paging<AsyncAreaTaxCodeReleationEntity> taxCodeReleationEntity = null;
        Map<String,String> params1 = new HashMap<String,String>(3);
//        params1.put("nsrdq",dqs);
        params1.put("rulesz",szs);
        taxCodeReleationEntity = taxCodeReleationService.queryHistoricalData(params1,null,null);
        if(taxCodeReleationEntity.getData().size()<=0){
            log.warn(dqs+"-"+szs+"满足的税种关系配置没有找到,请先配置税种关系");
        }else{
            Set<String> codes = new HashSet<String>();
            for(AsyncAreaTaxCodeReleationEntity e:taxCodeReleationEntity.getData()){
                if(StringUtils.isEmpty(e.dataszcode)){
                    log.warn(dqs+"-"+szs+"税种关系配置不能为空：->"+e.dataszcode);
                }else{
                    codes.add(e.dataszcode);
                }
            }
            originSzCodes = new ArrayList<String>(codes);
        }
    }


    public void TransferServiceDispatch(String status,String id,String szs, String dqs) {
        AsyncQueryHistoricalDataInfoEntity instancedata= null;
        try {
            Map<String,String> params = new HashMap<String,String>(2);
            if(StringUtils.isNotEmpty(dqs)){
                params.put("nsrdq",dqs);
            }
            if(!originSzCodes.isEmpty()){
                params.put("datasz", org.apache.commons.lang3.StringUtils.join(originSzCodes,","));
            }
            if(status!=null && status.equals("1")){
                params.put("status",status);//默认拉去待处理的
            }
            if(id!=null && !id.trim().equals("")){
                params.put("id",id);
            }
            Paging<AsyncQueryHistoricalDataInfoEntity> paging = null;
            if(noondealing){
                paging  = crawleredDataService.queryHistoricalData(null,params,1,Integer.parseInt(properties.getProperty("limit")));
                List<AsyncQueryHistoricalDataInfoEntity> data = paging.getData();
                if(data.size()>0){
                    noondealing = false;
                    log.info("当前拉取了"+data.size()+"条数据,正在处理");
                    String ruleszcode = "";
                    for(AsyncQueryHistoricalDataInfoEntity historicalDataInfoEntity :data){
                        if(historicalDataInfoEntity.getZhState()!=null&&historicalDataInfoEntity.getZhState()==1){
                        }else{
                            continue;
                        }
                        Map<String,Object> result = new HashMap<String,Object>();
                        instancedata = historicalDataInfoEntity;
                        historicalDataInfoEntity.setZhState(2);
                        historicalDataInfoEntity.setZhTime(sjctime.format(new Date()));
                        asyncQueryStockDataOperation.save(historicalDataInfoEntity);
                        try{
                            Map<String, String> sbdata = null;
                            if(historicalDataInfoEntity.getSbData()!=null){
                                try{
                                    sbdata = (Map<String,String>) JSON.toJSON(historicalDataInfoEntity.getSbData());
                                }catch (Exception e){
                                    e.printStackTrace();
                                    result.put("code","error");
                                    result.put("fianlresult","转化出错");
                                    result.put("message","转化成JSON格式报文数据出错");
                                    result.put("data",new JSONObject());
                                    log.error(TimeUtils.getCurrentDateTime(new Date(),TimeUtils.sdf1)+historicalDataInfoEntity.getId()+"转化成JSON格式报文数据出错");
                                   new SkipException("");
                                }
                                for(Map.Entry<String,String> map :sbdata.entrySet()){
                                    sbdata.put(map.getKey(), Base64Util.decode(map.getValue()));
                                }

                                Paging<AsyncAreaTaxCodeReleationEntity> taxCodeReleationEntity = null;
                                Map<String,String> params1 = new HashMap<String,String>(3);
//                                params1.put("nsrdq",historicalDataInfoEntity.getNsrdq());
                                params1.put("datasz",historicalDataInfoEntity.getSzdm());
                                taxCodeReleationEntity = taxCodeReleationService.queryHistoricalData(params1,null,null);
                                if(taxCodeReleationEntity.getData().size()<=0){
                                    log.warn(historicalDataInfoEntity.getNsrdq()+"-"+ruleszcode+"转化失败!税种关系配置没有找到,请先配置税种关系");
                                    result.put("code","fail");
                                    result.put("fianlresult","转化失败!");
                                    result.put("message","税种关系配置没有找到,请先配置税种关系");
                                    result.put("data",new JSONObject());
                                   new SkipException("");
                                }else{
                                    ruleszcode = taxCodeReleationEntity.getData().get(0).getRuleszcode();
                                }
                                params.clear();

                                if(historicalDataInfoEntity.getDataType().equals("json")){
                                    params.put("rulesz",ruleszcode);
                                    params.put("nsrdq",historicalDataInfoEntity.getNsrdq());
                                    params.put("type","json");
                                    params.put("status","1");
                                    List<AsyncBusinessTransferRuleEntity> transferRuleEntity = transferRuleDataService.queryHistoricalData(params,null,null).getData();
                                    if(transferRuleEntity.size()<=0){
                                        log.warn(historicalDataInfoEntity.getNsrdq()+"-"+ruleszcode+"没有上传对应文件或者规则未启用");
                                        result.put("code","fail");
                                        result.put("fianlresult","转化失败!");
                                        result.put("message","没有上传此税种对应规则文件或者规则未启用");
                                        result.put("data",new JSONObject());
                                       new SkipException("");
                                    }else{
                                        result=transferJsonDataService.majarTransferJsonData(sbdata,historicalDataInfoEntity.getNsrdq(),ruleszcode,null,null,transferRuleEntity);
                                    }
                                }else if(historicalDataInfoEntity.getDataType().equals("html")){
                                    params.put("rulesz",ruleszcode);
                                    params.put("nsrdq",historicalDataInfoEntity.getNsrdq());
                                    params.put("type","html");
                                    params.put("status","1");
                                    List<AsyncBusinessTransferRuleEntity> transferRuleEntity = transferRuleDataService.queryHistoricalData(params,null,null).getData();
                                    if(transferRuleEntity.size()<=0){
                                        log.warn(historicalDataInfoEntity.getNsrdq()+"-"+ruleszcode+"没有上传对应文件或者规则未启用");
                                        result.put("code","fail");
                                        result.put("fianlresult","转化失败!");
                                        result.put("message","没有上传此税种对应文件或者未启用符合的规则文件");
                                        result.put("data",new JSONObject());
                                       new SkipException("");
                                    }else{
                                        result =transferHtmlDataService.majarTransferHtmlData(sbdata,historicalDataInfoEntity.getNsrdq(),ruleszcode,null,null,transferRuleEntity);
                                    }
                                }else{
                                    result.put("code","fail");
                                    result.put("fianlresult","转化失败!"+historicalDataInfoEntity.getId()+"数据源格式不是[json/html]:"+historicalDataInfoEntity.getDataType());
                                    result.put("data",new JSONObject());
                                    log.warn("请检查处理:获取数据源的dataType不是[josn/html],而是:"+historicalDataInfoEntity.getDataType());
                                   new SkipException("");
                                }
                                try{
                                    historicalDataInfoEntity.setDataOut(Base64Util.encode(JSON.toJSONString(result.get("data")).replaceAll("(\\\\r\\\\n|\\\\r|\\\\n|\\\\n\\\\r| )", "")));
                                }catch (Exception eee){
                                    eee.printStackTrace();
                                    log.info("输出结果报文json转化异常"+historicalDataInfoEntity.getId()+"："+result.get("data"));
                                    result.put("code","fail");
                                    result.put("fianlresult","报文结果保存失败!");
                                    result.put("data",new JSONObject());
                                   new SkipException("");
                                }
                                result.remove("data");
                                historicalDataInfoEntity.setResult(Base64Util.encode(JSON.toJSONString(result)));
                            }else{
                                result.put("code","fail");
                                result.put("data",new JSONObject());
                                result.put("fianlresult","转化失败!");
                                result.put("message","原始报文数据为空");
                                historicalDataInfoEntity.setDataOut(Base64Util.encode(JSON.toJSONString(result.get("data")).replaceAll("(\\\\r\\\\n|\\\\r|\\\\n|\\\\n\\\\r| )", "")));
                                result.remove("data");
                                historicalDataInfoEntity.setResult(Base64Util.encode(JSON.toJSONString(result)));
                               new SkipException("");
                            }
                            asyncQueryStockDataOperation.save(historicalDataInfoEntity);
                        }catch (SkipException e){
                        }catch (Exception e){
                            result.put("code","fail");
                            result.put("data",new JSONObject());
                            result.put("fianlresult","转化失败!");
                            result.put("message","转化异常数据ID:"+historicalDataInfoEntity.getId()+"message:"+JSON.toJSONString(result));
                            e.printStackTrace();
                            log.error(TimeUtils.getCurrentDateTime(new Date(),TimeUtils.sdf4)+"转化异常"+historicalDataInfoEntity.getNsrdq()+"-"+ruleszcode+"\n"+e.getMessage());
                            return;
                        }finally {
                            if(result.get("code")==null){
                                historicalDataInfoEntity.setZhState(109);
                                log.warn("存在记录转化状态未捕获到"+historicalDataInfoEntity.getId());
                            }else if(result.get("code").equals("error")){
                                historicalDataInfoEntity.setZhState(101);
                            }else if(result.get("code").equals("warn")){
                                historicalDataInfoEntity.setZhState(102);
                            }else if(result.get("code").equals("fail")){
                                historicalDataInfoEntity.setZhState(103);
                            }else if(result.get("code").equals("succsess")){
                                historicalDataInfoEntity.setZhState(3);
                            }else{
                                log.warn("存在记录转化状态未知"+historicalDataInfoEntity.getId()+result.get("code"));
                                historicalDataInfoEntity.setZhState(109);
                            }
                            historicalDataInfoEntity.setResult(Base64Util.encode(JSON.toJSONString(result)));
                            asyncQueryStockDataOperation.save(historicalDataInfoEntity);
                        }
                    }
                }else{
                    log.info("地区:"+dqs+"->税种"+szs+"->数据数量为0");
                }
            }
        } catch (Exception e) {
            Map<String,String> r = new HashMap<String,String>();
            r.put("code","fail");
            r.put("message","发生异常信息:抓取信息"+e.getMessage());
            if(instancedata!=null){
                instancedata.setZhState(3);
                instancedata.setResult(JSON.toJSONString(r));
            }
            e.printStackTrace();
            log.error("定时批量操作发生未知异常:", e.getMessage());
        }finally {
            noondealing = true;
            if(instancedata!=null){
                asyncQueryStockDataOperation.save(instancedata);
            }
        }
    }
    static SimpleDateFormat sjctime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public void TransferService(String status,String id,String szs, String dqs) {
        AsyncQueryHistoricalDataInfoEntity instancedata= null;
        try {
            Map<String,String> params = new HashMap<String,String>(2);
            if(StringUtils.isNotEmpty(dqs)){
                params.put("nsrdq",dqs);
            }
            if(!originSzCodes.isEmpty()){
                params.put("datasz", org.apache.commons.lang3.StringUtils.join(originSzCodes,","));
            }
            if(status!=null && status.equals("1")){
                params.put("status",status);//默认拉去待处理的
            }
            if(id!=null && !id.trim().equals("")){
                params.put("id",id);
            }
            Paging<AsyncQueryHistoricalDataInfoEntity> paging = null;
            paging  = crawleredDataService.queryHistoricalData(null,params,1,Integer.parseInt(properties.getProperty("limit")));
            List<AsyncQueryHistoricalDataInfoEntity> data = paging.getData();
            if(data.size()>0){
                log.info("对应id数据正在处理");
                String ruleszcode = "";
                for(AsyncQueryHistoricalDataInfoEntity historicalDataInfoEntity :data){
                    Map<String,Object> result = new HashMap<String,Object>();
                    instancedata = historicalDataInfoEntity;
                    historicalDataInfoEntity.setZhState(2);
                    historicalDataInfoEntity.setZhTime(sjctime.format(new Date()));
                    asyncQueryStockDataOperation.save(historicalDataInfoEntity);
                    try{
                        Map<String, String> sbdata = null;
                        if(historicalDataInfoEntity.getSbData()!=null){
                            try{
                                sbdata = (Map<String,String>) JSON.toJSON(historicalDataInfoEntity.getSbData());
                            }catch (Exception e){
                                e.printStackTrace();
                                result.put("code","error");
                                result.put("fianlresult","转化出错");
                                result.put("message","转化成JSON格式报文数据出错");
                                result.put("data",new JSONObject());
                                log.error(TimeUtils.getCurrentDateTime(new Date(),TimeUtils.sdf1)+historicalDataInfoEntity.getId()+"转化成JSON格式报文数据出错");
                                new SkipException("");
                            }
                            for(Map.Entry<String,String> map :sbdata.entrySet()){
                                sbdata.put(map.getKey(), Base64Util.decode(map.getValue()));
                            }

                            Paging<AsyncAreaTaxCodeReleationEntity> taxCodeReleationEntity = null;
                            Map<String,String> params1 = new HashMap<String,String>(3);
//                                params1.put("nsrdq",historicalDataInfoEntity.getNsrdq());
                            params1.put("datasz",historicalDataInfoEntity.getSzdm());
                            taxCodeReleationEntity = taxCodeReleationService.queryHistoricalData(params1,null,null);
                            if(taxCodeReleationEntity.getData().size()<=0){
                                log.warn(historicalDataInfoEntity.getNsrdq()+"-"+ruleszcode+"转化失败!税种关系配置没有找到,请先配置税种关系");
                                result.put("code","fail");
                                result.put("fianlresult","转化失败!");
                                result.put("message","税种关系配置没有找到,请先配置税种关系");
                                result.put("data",new JSONObject());
                                new SkipException("");
                            }else{
                                ruleszcode = taxCodeReleationEntity.getData().get(0).getRuleszcode();
                            }
                            params.clear();

                            if(historicalDataInfoEntity.getDataType().equals("json")){
                                params.put("rulesz",ruleszcode);
                                params.put("nsrdq",historicalDataInfoEntity.getNsrdq());
                                params.put("type","json");
                                params.put("status","1");
                                List<AsyncBusinessTransferRuleEntity> transferRuleEntity = transferRuleDataService.queryHistoricalData(params,null,null).getData();
                                if(transferRuleEntity.size()<=0){
                                    log.warn(historicalDataInfoEntity.getNsrdq()+"-"+ruleszcode+"没有上传对应文件或者规则未启用");
                                    result.put("code","fail");
                                    result.put("fianlresult","转化失败!");
                                    result.put("message","没有上传此税种对应规则文件或者规则未启用");
                                    result.put("data",new JSONObject());
                                    new SkipException("");
                                }else{
                                    result=transferJsonDataService.majarTransferJsonData(sbdata,historicalDataInfoEntity.getNsrdq(),ruleszcode,null,null,transferRuleEntity);
                                }
                            }else if(historicalDataInfoEntity.getDataType().equals("html")){
                                params.put("rulesz",ruleszcode);
                                params.put("nsrdq",historicalDataInfoEntity.getNsrdq());
                                params.put("type","html");
                                params.put("status","1");
                                List<AsyncBusinessTransferRuleEntity> transferRuleEntity = transferRuleDataService.queryHistoricalData(params,null,null).getData();
                                if(transferRuleEntity.size()<=0){
                                    log.warn(historicalDataInfoEntity.getNsrdq()+"-"+ruleszcode+"没有上传对应文件或者规则未启用");
                                    result.put("code","fail");
                                    result.put("fianlresult","转化失败!");
                                    result.put("message","没有上传此税种对应文件或者未启用符合的规则文件");
                                    result.put("data",new JSONObject());
                                    new SkipException("");
                                }else{
                                    result =transferHtmlDataService.majarTransferHtmlData(sbdata,historicalDataInfoEntity.getNsrdq(),ruleszcode,null,null,transferRuleEntity);
                                }
                            }else{
                                result.put("code","fail");
                                result.put("fianlresult","转化失败!"+historicalDataInfoEntity.getId()+"数据源格式不是[json/html]:"+historicalDataInfoEntity.getDataType());
                                result.put("data",new JSONObject());
                                log.warn("请检查处理:获取数据源的dataType不是[josn/html],而是:"+historicalDataInfoEntity.getDataType());
                                new SkipException("");
                            }
                            try{
                                historicalDataInfoEntity.setDataOut(Base64Util.encode(JSON.toJSONString(result.get("data")).replaceAll("(\\\\r\\\\n|\\\\r|\\\\n|\\\\n\\\\r| )", "")));
                            }catch (Exception eee){
                                eee.printStackTrace();
                                log.info("输出结果报文json转化异常"+historicalDataInfoEntity.getId()+"："+result.get("data"));
                                result.put("code","fail");
                                result.put("fianlresult","报文结果保存失败!");
                                result.put("data",new JSONObject());
                                new SkipException("");
                            }
                            result.remove("data");
                            historicalDataInfoEntity.setResult(Base64Util.encode(JSON.toJSONString(result)));
                        }else{
                            result.put("code","fail");
                            result.put("data",new JSONObject());
                            result.put("fianlresult","转化失败!");
                            result.put("message","原始报文数据为空");
                            historicalDataInfoEntity.setDataOut(Base64Util.encode(JSON.toJSONString(result.get("data")).replaceAll("(\\\\r\\\\n|\\\\r|\\\\n|\\\\n\\\\r| )", "")));
                            result.remove("data");
                            historicalDataInfoEntity.setResult(Base64Util.encode(JSON.toJSONString(result)));
                            new SkipException("");
                        }
                        asyncQueryStockDataOperation.save(historicalDataInfoEntity);
                    }catch (SkipException e){
                    }catch (Exception e){
                        result.put("code","fail");
                        result.put("data",new JSONObject());
                        result.put("fianlresult","转化失败!");
                        result.put("message","转化异常数据ID:"+historicalDataInfoEntity.getId()+"message:"+JSON.toJSONString(result));
                        e.printStackTrace();
                        log.error(TimeUtils.getCurrentDateTime(new Date(),TimeUtils.sdf4)+"转化异常"+historicalDataInfoEntity.getNsrdq()+"-"+ruleszcode+"\n"+e.getMessage());
                        return;
                    }finally {
                        if(result.get("code")==null){
                            historicalDataInfoEntity.setZhState(109);
                            log.warn("存在记录转化状态未捕获到"+historicalDataInfoEntity.getId());
                        }else if(result.get("code").equals("error")){
                            historicalDataInfoEntity.setZhState(101);
                        }else if(result.get("code").equals("warn")){
                            historicalDataInfoEntity.setZhState(102);
                        }else if(result.get("code").equals("fail")){
                            historicalDataInfoEntity.setZhState(103);
                        }else if(result.get("code").equals("succsess")){
                            historicalDataInfoEntity.setZhState(3);
                        }else{
                            log.warn("存在记录转化状态未知"+historicalDataInfoEntity.getId()+result.get("code"));
                            historicalDataInfoEntity.setZhState(109);
                        }
                        historicalDataInfoEntity.setResult(Base64Util.encode(JSON.toJSONString(result)));
                        asyncQueryStockDataOperation.save(historicalDataInfoEntity);
                    }
                }
            }else{
                log.info("对应id数据数量为0");
            }
        } catch (Exception e) {
            Map<String,String> r = new HashMap<String,String>();
            r.put("code","fail");
            r.put("message","发生异常信息:抓取信息"+e.getMessage());
            if(instancedata!=null){
                instancedata.setZhState(3);
                instancedata.setResult(JSON.toJSONString(r));
            }
            e.printStackTrace();
            log.error("操作发生未知异常:", e.getMessage());
        }finally {
            if(instancedata!=null){
                asyncQueryStockDataOperation.save(instancedata);
            }
        }
    }


    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    /**
     * 在ScheduledFuture中有一个cancel可以停止定时任务。
     */
    public static ConcurrentHashMap<String,ScheduledFuture<?>>  futures = new ConcurrentHashMap<>(50);

    public static String initNewfuture(ScheduledFuture<?> future){
        String futureid = UUIDUtils.get16UUID();
        futures.put("task"+futureid,future);
        return "task"+futureid;
    }

    /***
     * 定时任务调度配置管理
     */


    /**
     * ThreadPoolTaskScheduler：线程池任务调度类，能够开启线程池进行任务调度。
     * ThreadPoolTaskScheduler.schedule()方法会创建一个定时计划ScheduledFuture，在这个方法需要添加两个参数，Runnable（线程接口类） 和CronTrigger（定时任务触发器）
     * @return
     */
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler =  new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(8);
        return threadPoolTaskScheduler;
    }


    public String startSchedule(AsyncTaskTimerEntity task){
        if(task.getTid()!=null&&futures.get(task.getTid())!=null&&futures.get(task.getTid())!=null){
            futures.get(task.getTid()).cancel(true);
        }
        String rate = task.getExpressContent();
        ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                executeTransferService(task.getSzs(),task.getNsrdqs(),task.isdelay,task.getDelayTime(), SchedualEnum.fixrate,task.getExpressContent());
            }
        }, new CronTrigger("0/"+rate+" * * * * *"));
        if(task.getTid()!=null&&futures.keySet().contains(task.getTid())){
            futures.put(task.getTid(),future);
            return task.getTid();
        }else{
            return initNewfuture(future);
        }
    }

    public  Map<String,String> stopSchedule(AsyncTaskTimerEntity task){
        Map<String,String> result = new HashMap<String,String>(4);
        if(task.getTid()!=null&&futures.get(task.getTid()) !=null){
            futures.get(task.getTid()).cancel(true);
            futures.remove(task.getTid());
        }else{
            result.put("code","fail");
            result.put("message","该任务未启动,请刷新页面后操作");
            return result;
        }
        result.put("code","success");
        result.put("message","已停止");
        return result;

    }


    public void initBootSchedual()throws Exception{
        Map<String,String> params = new HashMap<String,String>(0);
        Paging<AsyncTaskTimerEntity> tasks = taskManagerService.queryHistoricalData(params,null,null);
        if(tasks.getData()!=null&&tasks.getData().size()>0){
            for(AsyncTaskTimerEntity task:tasks.getData()){
                if(task.getStatus()!=null&&task.getStatus().equals("1")){
                    String taskid = startSchedule(task);
                    params.put("status","1");
                    params.put("id",task.getId());
                    params.put("update","update");
                    params.put("startTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    params.put("endTime","");
                    params.put("taskid",taskid);
                    params.put("start","start");
                    taskManagerService.updateTaskDataByid(params);
                }
            }
        }
    }
}

class SkipException extends RuntimeException{
    public SkipException(String message) {
        super(message);
    }
}