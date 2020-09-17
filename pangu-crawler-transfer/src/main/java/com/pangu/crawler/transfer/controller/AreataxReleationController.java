package com.pangu.crawler.transfer.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncAreaTaxCodeReleationEntity;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncBusinessTransferRuleEntity;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.Paging;
import com.pangu.crawler.framework.utils.StringUtils;
import com.pangu.crawler.transfer.service.TaxCodeReleationService;
import com.pangu.crawler.transfer.service.TransferRuleDataService;
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

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
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
@RequestMapping("taxcodereleation")
public class AreataxReleationController {
    /***
     * 查询爬取的结果存量数据
     */
    @Autowired
    TaxCodeReleationService taxCodeReleationService;
    @ResponseBody
    @RequestMapping("/queryreleationData")
    public JSON queryHistoricalData(Integer page, Integer limit, HttpServletRequest request) throws Exception {
        Map<String,String> map = new HashMap<String,String>(7);

        if(StringUtils.isNotEmpty(request.getParameter("nsrdq"))){
            map.put("nsrdq",request.getParameter("nsrdq"));
        }
        if(StringUtils.isNotEmpty(request.getParameter("sz"))){
            map.put("rulesz",request.getParameter("sz"));
        }
        Paging<AsyncAreaTaxCodeReleationEntity> paging = taxCodeReleationService.queryHistoricalData(map,page,limit);
        JSONObject json = new JSONObject();
        json.put("code","0");
        json.put("msg","");
        json.put("count",paging.getTotalCount());
        json.put("data",paging.getJsondata());
        return json;
    }

    @ResponseBody
    @PostMapping("/uploadReleationByid")
    public Map<String,String> uploadReleationByid(HttpServletRequest request) throws Exception {
        Map<String,String> result = new HashMap<String,String>(4);
        try{
            String rule_nsrdq = request.getParameter("rule_nsrdq");
            String rule_szname = request.getParameter("rule_szname");
            String rule_sz = request.getParameter("rule_sz");
            String data_szcode = request.getParameter("data_szcode");
            Map<String,String> map = new HashMap<String,String>(4);
            if(StringUtils.isNotEmpty(rule_nsrdq)){
                map.put("rule_nsrdq",rule_nsrdq);
            }
            if(StringUtils.isNotEmpty(rule_szname)){
                map.put("rule_szname",rule_szname);
            }
            if(StringUtils.isNotEmpty(rule_sz)){
                map.put("rule_szcode",rule_sz);
            }
            if(StringUtils.isNotEmpty(data_szcode)){
                map.put("data_szcode",data_szcode);
            }
            result = taxCodeReleationService.updateTaxReleationDataByid(map);
        }catch (Exception e){
            e.printStackTrace();
            log.error("添加规则失败!"+e.getMessage() );
        }finally {
            return result;
        }


    }


   /* @ResponseBody
    @PostMapping("uploadRuleStatus")
    public Map<String,String> uploadRuleStatus(HttpServletRequest request) throws Exception {
        Map<String,String> result = new HashMap<String,String>(4);
        try{
            String rule_status = request.getParameter("rule_status");
            String id = request.getParameter("id");
            result = taxCodeReleationService.uploadRuleStatusByid(id,rule_status);
        }catch (Exception e){
            e.printStackTrace();
            log.error("添加规则失败!"+e.getMessage() );
        }finally {
            return result;
        }
    }*/

    /*@ResponseBody
    @PostMapping("checkRuleLogic")
    public Map<String,Object> checkRuleLogic(HttpServletRequest request) throws Exception {
        Map<String,Object> result = new HashMap<String,Object>(4);
        try{
            String content = request.getParameter("content");
            String id = request.getParameter("id");
            String nsrdq = request.getParameter("nsrdq");
            String sz = request.getParameter("sz");
            String formid = request.getParameter("formid");
            String type = request.getParameter("type");
            String ruleobjid = request.getParameter("ruleobjid");
            result = taxCodeReleationService.checkRuleLogic(id,nsrdq,sz,formid,type,content,ruleobjid);
        }catch (Exception e){
            e.printStackTrace();
            result.put("code","fail");
            result.put("message",e.getMessage());
            log.error("添加规则失败!"+e.getMessage() );
        }finally {
            return result;
        }
    }*/

}
