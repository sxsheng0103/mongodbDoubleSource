package com.pangu.crawler.transfer.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.PanguCrawlerBootStrap;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncAreaTaxCodeReleationEntity;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncBusinessTransferRuleEntity;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncTaskTimerEntity;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.Paging;
import com.pangu.crawler.transfer.service.TaskManagerService;
import com.pangu.crawler.transfer.service.TaxCodeReleationService;
import com.pangu.crawler.transfer.service.TransferRuleDataService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author sheng.ding
 * @Date 2020/8/3 9:52
 * @Version 1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PanguCrawlerBootStrap.class)
@Slf4j
@Service
public class TransferTest {

    @Autowired
    TaskManagerService taskManagerService;

    @Autowired
    TaxCodeReleationService taxCodeReleationService;

    @Autowired
    TransferRuleDataService transferRuleDataService;
    @Autowired
    TransferRuleDataService dataService;
    @Test
    public void testrulelogic(){
        Map<String,Object> result = new HashMap<String,Object>(4);
        try{
            String id = "5f238ae84c11ad02c03b6f1f";//  5f2391fc2ceb1505a0600840
            String nsrdq = "zhejiang";//shanghai
            String sz = "fjs";//zzsybnsr
            String formid = "zzssyyybnsr04bqjxsemxb";                              //zzsybnsr&fjs             zzssyyybnsrzb               zzssyyybnsr04bqjxsemxb
            String type = "json";
            String ruleobjid = "5f290287d5807d5c3c75e169";              //5f23e7f7de42c93928d07fd5    5f277cd3d5807d36a89f410c    5f278070d5807d6ebc77e382
            int times = 100;
            for(int i =0;i<times;i++){
                String start = "start";
                try{
                    result = dataService.checkRuleLogic(id,nsrdq,sz,formid,type,"",ruleobjid);
                }catch (Exception e){
                    e.printStackTrace();
                }
                String stop = "stop";
            }


        }catch (Exception e){
            e.printStackTrace();
            result.put("code","fail");
            result.put("message",e.getMessage());
            log.error("添加规则失败!"+e.getMessage() );
        }finally {
        }
    }

    /**
     * 备份数据
     */
    @Test
    public void TransferServiceDispatch(String path) throws Exception{
        try{
            File outpath = null;
            if(path!=null&&!path.equals("")){
                outpath = new File(path+"\\..\\file\\"+new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date()));
            }else{
                outpath = new File("D:\\file\\"+new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date()));
            }
            outpath.mkdirs();
            Paging<AsyncTaskTimerEntity> taskTimerEntity= taskManagerService.queryHistoricalData(Collections.emptyMap(),null,null);
            if(taskTimerEntity.getData().size()>0){
                JSONArray result =  (JSONArray)JSON.toJSON(taskTimerEntity.getData());
                for(int num = 0;num<result.size();num++){
                    JSONObject o = (JSONObject)result.get(num);
                    o.put("_id","ObjectId(\""+o.getString("id")+"\")");
                    o.remove("id");
                    o.put("_class",AsyncTaskTimerEntity.class.getName());
                }
                File outFile = new File(outpath.getAbsolutePath()+"\\async_task_timer.json");
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8");
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.write(JSON.toJSONString(result));
                bufferedWriter.flush();
                bufferedWriter.close();
                writer.close();
            }
            Paging<AsyncAreaTaxCodeReleationEntity> areaTaxCodeReleationEntity = taxCodeReleationService.queryHistoricalData(Collections.emptyMap(),null,null);
            if(areaTaxCodeReleationEntity.getData().size()>0){
                JSONArray result =  (JSONArray)JSON.toJSON(areaTaxCodeReleationEntity.getData());
                for(int num = 0;num<result.size();num++){
                    JSONObject o = (JSONObject)result.get(num);
                    o.put("_id","ObjectId(\""+o.getString("id")+"\")");
                    o.remove("id");
                    o.put("_class",AsyncAreaTaxCodeReleationEntity.class.getName());
                }
                File outFile = new File(outpath.getAbsolutePath()+"\\async_area_tax_code_releation.json");
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8");
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.write(JSON.toJSONString(result));
                bufferedWriter.flush();
                bufferedWriter.close();
                writer.close();
            }
            Paging<AsyncBusinessTransferRuleEntity> businessTransferRuleEntity = transferRuleDataService.queryHistoricalData(Collections.emptyMap(),null,null);
            if(businessTransferRuleEntity.getData().size()>0){
                JSONArray result =  (JSONArray)JSON.toJSON(businessTransferRuleEntity.getData());
                for(int num = 0;num<result.size();num++){
                    JSONObject o = (JSONObject)result.get(num);
                    o.put("_id","ObjectId(\""+o.getString("id")+"\")");
                    o.remove("id");
                    o.put("_class",AsyncBusinessTransferRuleEntity.class.getName());
                }
                File outFile = new File(outpath.getAbsolutePath()+"\\async_business_transfer_rule.json");
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8");
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.write(JSON.toJSONString(result));
                bufferedWriter.flush();
                bufferedWriter.close();
                writer.close();
            }
        log.info("备份数据完成");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
