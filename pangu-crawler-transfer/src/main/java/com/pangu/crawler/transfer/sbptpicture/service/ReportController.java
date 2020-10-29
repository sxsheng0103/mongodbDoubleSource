package com.pangu.crawler.transfer.sbptpicture.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncBusinessTransferRuleEntity;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.Paging;
import com.pangu.crawler.framework.utils.Base64Util;
import com.pangu.crawler.framework.utils.StringUtils;
import com.pangu.crawler.transfer.sbptpicture.mongo.AsyncQueryBusinessPictureEntity;
import com.pangu.crawler.transfer.service.TransferRuleDataService;
import com.pangu.crawler.transfer.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author sheng.ding
 * @Date 2020/7/20 12:13
 * @Version 1.0
 **/
@Controller
@Slf4j
@RequestMapping("/report")
public class ReportController {
    /***
     * 保存申报结果图片
     */
    @Autowired
    ReportService reportService;
    @PostMapping
    @ResponseBody
    @RequestMapping("/queryreportcation")
    public Map<String,Object> queryHistoricalData(HttpServletRequest request) throws Exception {
        Map<String,Object> result = new HashMap<String,Object>(3);
        try{
            Map<String,String> map = new HashMap<String,String>(7);
            if(StringUtils.isNotEmpty(request.getParameter("jglx"))){
                map.put("jglx",request.getParameter("jglx"));
            }
            if(StringUtils.isNotEmpty(request.getParameter("name"))){
                map.put("name",request.getParameter("name"));
            }
            if(StringUtils.isNotEmpty(request.getParameter("lsh"))){
                map.put("lsh",request.getParameter("lsh"));
            }
            if(StringUtils.isNotEmpty(request.getParameter("releationid"))){
                map.put("releationid",request.getParameter("releationid"));
            }
            if(StringUtils.isNotEmpty(request.getParameter("id"))){
                map.put("id",request.getParameter("id"));
            }
//127.0.0.1:8086/report/queryreportcation?jglx=1&name=110423NDj&lsh=111&releationid=121231&id=5f992e8a6279bd61a8a8f847
            List<AsyncQueryBusinessPictureEntity> data = reportService.queryHistoricalData(map);
            String message = "成功";
            if(data.size()==1){
                result.put("data",data.get(0));
            }else{
                result.put("data",null);
                 message = "对应条件查询到"+data.size()+"条数据!";
            }
            result.put("code","success");
            result.put("message",message);
        }catch (Exception e){
            log.error("查询错误!");
        }finally {
            return  result;
        }
    }
//127.0.0.1:8086/report/savereportcation?jglx=1&name=110423NDj&lsh=111&releationid=121231&screenbase64=weqrwqrqwrqwer
    @ResponseBody
    @PostMapping("/savereportcation")
    public Map<String,Object> savereportcation(HttpServletRequest request) throws Exception {
        Map<String,Object> result = new HashMap<String,Object>(2);
        try{
            Map<String,String> map = new HashMap<String,String>(7);
            if(StringUtils.isNotEmpty(request.getParameter("jglx"))){
                map.put("jglx",request.getParameter("jglx"));
            }
            if(StringUtils.isNotEmpty(request.getParameter("ip"))){
                map.put("ip",request.getParameter("ip"));
            }
            if(StringUtils.isNotEmpty(request.getParameter("computername"))){
                map.put("computername",request.getParameter("computername"));
            }
            if(StringUtils.isNotEmpty(request.getParameter("name"))){
                map.put("name",request.getParameter("name"));
            }
            if(StringUtils.isNotEmpty(request.getParameter("lsh"))){
                map.put("lsh",request.getParameter("lsh"));
            }
            if(StringUtils.isNotEmpty(request.getParameter("releationid"))){
                map.put("releationid",request.getParameter("releationid"));
            }
            if(StringUtils.isNotEmpty(request.getParameter("screenbase64"))){
                map.put("screenbase64", Base64Util.encode(request.getParameter("screenbase64")));
            }
            result =  reportService.savereportcation(map);
        }catch (Exception e){
            e.printStackTrace();
            log.error("保存申报图片失败!"+ TimeUtils.getCurrentDateTime(new Date(),TimeUtils.sdf2) +e.getMessage() );
        }finally {
            return result;
        }
    }

}
