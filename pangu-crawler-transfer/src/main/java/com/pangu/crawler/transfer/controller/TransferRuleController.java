package com.pangu.crawler.transfer.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncBusinessTransferRuleEntity;
import com.pangu.crawler.business.dao.mongoDB.operation.auxiliaryutils.Paging;
import com.pangu.crawler.framework.utils.StringUtils;
import com.pangu.crawler.transfer.service.TransferRuleDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
@RequestMapping("transferrule")
public class TransferRuleController {
    /***
     * 查询爬取的结果存量数据
     */
    @Autowired
    TransferRuleDataService dataService;
    @ResponseBody
    @RequestMapping("/queryTransferRuleData")
    public JSON queryHistoricalData(Integer page, Integer limit, HttpServletRequest request) throws Exception {
        Map<String,String> map = new HashMap<String,String>(7);
        if(StringUtils.isNotEmpty(request.getParameter("nsrsbh"))){
            map.put("nsrsbh",request.getParameter("nsrsbh"));
        }
        if(StringUtils.isNotEmpty(request.getParameter("nsrdq"))){
            map.put("nsrdq",request.getParameter("nsrdq"));
        }
        if(StringUtils.isNotEmpty(request.getParameter("sz"))){
            map.put("rulesz",request.getParameter("sz"));
        }
        if(StringUtils.isNotEmpty(request.getParameter("type"))){
            map.put("type",request.getParameter("type"));
        }
        if(StringUtils.isNotEmpty(request.getParameter("formid"))){
            map.put("formid",request.getParameter("formid"));
        }
        if(StringUtils.isNotEmpty(request.getParameter("status"))){
            map.put("status",request.getParameter("status"));
        }
        Paging<AsyncBusinessTransferRuleEntity> paging = dataService.queryHistoricalData(map,page,limit);
        JSONObject json = new JSONObject();
        json.put("code","0");
        json.put("msg","");
        json.put("count",paging.getTotalCount());
        json.put("data",paging.getJsondata());
        return json;
    }

    @ResponseBody
    @PostMapping("/uploadTrasferRuleFile")
    public Map<String,String> uploadTrasferRuleFile(HttpServletRequest request) throws Exception {
        Map<String,String> result = new HashMap<String,String>(4);
        try{
            String rule_nsrdq = request.getParameter("rule_nsrdq");
            String rule_formid = request.getParameter("rule_formid");
            String rule_sz = request.getParameter("rule_sz");
            String rule_type = request.getParameter("rule_type");
            MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
            MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);
            MultipartFile file1  = multipartRequest.getFile("file");
            if(file1==null){
                result.put("code","fail");
                result.put("message","上传的文件为空");
                return result;
            }
            //判断TXT文件编码格式
            byte[] head = new byte[3];
            file1.getInputStream().read(head);
            String decode= "UTF-8";
            /*if (head[0] == -1 && head[1] == -2 ){
                //Unicode              -1,-2,84
                decode= "Unicode";
            }else if (head[0] == -2 && head[1] == -1 ){
                //Unicode big endian   -2,-1,0,84
                decode= "UTF-16";
            }else if(head[0]==-17 && head[1]==-69 && head[2] ==-65) {
                //UTF-8                -17,-69,-65,84

            }else{
                decode= "gb2312";
                //ANSI                  84 = T
            }*/
            List<String> rules = IOUtils.readLines(file1.getInputStream(), Charsets.toCharset(decode));


            Map<String,String> map = new HashMap<String,String>(7);
            if(StringUtils.isNotEmpty(rule_nsrdq)){
                map.put("rule_nsrdq",rule_nsrdq);
            }
            if(StringUtils.isNotEmpty(rule_formid)){
                map.put("rule_formid",rule_formid);
            }
            if(StringUtils.isNotEmpty(rule_sz)){
                map.put("rule_sz",rule_sz);
            }
            if(StringUtils.isNotEmpty(rule_type)){
                map.put("rule_type",rule_type);
            }
            result = dataService.updateTransferRuleDataByid(rules,map);
        }catch (Exception e){
            e.printStackTrace();
            log.error("添加规则失败!"+e.getMessage() );
        }finally {
            return result;
        }


    }


    @ResponseBody
    @PostMapping("uploadRuleStatus")
    public Map<String,String> uploadRuleStatus(HttpServletRequest request) throws Exception {
        Map<String,String> result = new HashMap<String,String>(4);
        try{
            String rule_status = request.getParameter("rule_status");
            String id = request.getParameter("id");
            result = dataService.uploadRuleStatusByid(id,rule_status);
        }catch (Exception e){
            e.printStackTrace();
            log.error("添加规则失败!"+e.getMessage() );
        }finally {
            return result;
        }
    }

    @ResponseBody
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
            result = dataService.checkRuleLogic(id,nsrdq,sz,formid,type,content,ruleobjid);
        }catch (Exception e){
            e.printStackTrace();
            result.put("code","fail");
            result.put("message",e.getMessage());
            log.error("添加规则失败!"+e.getMessage() );
        }finally {
            return result;
        }
    }

}
