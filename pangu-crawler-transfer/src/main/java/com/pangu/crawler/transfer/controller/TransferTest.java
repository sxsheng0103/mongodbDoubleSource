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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
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
     *     mongoexport -d foobar -c persons -o D:/persons.json
     *    如果要导入其他主机的数据库文档则这样写
     *    mongoexport --host 192.168.0.16 --port 37017
     *    mongoimport --db foobar --collection persons --file d:/persons.json
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



    public static void main(String[] args) {
        Map<String,String> s= new HashMap<>();
        s.put("jglx","1");
        s.put("name","110423NDj");
        s.put("lsh","111");
        s.put("releationid","121231");
        s.put("id","5f992e8a6279bd61a8a8f847");
        httpPost("http://127.0.0.1:8086/report/queryreportcation",s);
        s= new HashMap<>();
        s.put("jglx","1");
        s.put("name","110423");
        String ip = "";
        String  computername = "";
        try{
            InetAddress addr = InetAddress.getLocalHost();
            ip = addr.getHostAddress();
            computername = addr.getHostName();
        }catch (UnknownHostException e){
        }
        s.put("ip",ip);
        s.put("computername",computername);
        s.put("lsh","111");
        s.put("releationid","121231");
        s.put("screenbase64","weqrwqrqwrqwer");
        httpPost("http://127.0.0.1:8086/report/savereportcation",s);
    }

    private static String httpPost(String urlString, Map<String,String> data) {

        String dataStr = "";
        for(Map.Entry<String,String> property:data.entrySet()){
            dataStr+=property.getKey()+"="+property.getValue()+"&";
        }
        String response = "";
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setReadTimeout(30 * 1000);
            connection.setConnectTimeout(10 * 1000);
            connection.setRequestProperty("Content-Encoding", "utf-8");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("sbptmiyao", "RTdvVkJHWW0mQ3hYT1pVcXBaJSYhRk9YZiN3TGtoeXFYWSMjKjQjcllnb1kjTmFmWndeT2dNdF4kNWpvWlZmaQ==");
            byte[] request = dataStr.getBytes(StandardCharsets.UTF_8);
            String contentLength = String.valueOf(request.length);
            connection.setRequestProperty("Content-Length", contentLength);
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(request);
                outputStream.flush();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    response = readResponse(connection.getInputStream());
                } else {
                    response = readResponse(connection.getErrorStream());
                }
            }
        } catch (Exception e) {
            response = "{code:\"error\",\"info\":\""+e.getClass().getSimpleName()+"\"}";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }


    private static String readResponse(InputStream inputStream) throws Exception {
        byte[] responseBytes = new byte[0];
        while (true) {
            byte[] readedBytes = new byte[1024];
            int readedCount = inputStream.read(readedBytes);
            if (readedCount <= 0) {
                break;
            }
            byte[] newBytes = new byte[responseBytes.length + readedCount];
            System.arraycopy(responseBytes, 0, newBytes, 0, responseBytes.length);
            System.arraycopy(readedBytes, 0, newBytes, responseBytes.length, readedCount);
            responseBytes = newBytes;
        }
        StringBuilder readResponse = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(responseBytes), StandardCharsets.UTF_8))) {
            String line;
            for (line = br.readLine(); line != null; line = br.readLine()) {
                readResponse.append(line).append("\r\n");
            }
        }
        return readResponse.toString();
    }
}
