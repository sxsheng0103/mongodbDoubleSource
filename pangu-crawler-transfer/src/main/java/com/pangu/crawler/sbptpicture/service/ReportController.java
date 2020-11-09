package com.pangu.crawler.sbptpicture.service;

import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.framework.utils.StringUtils;
import com.pangu.crawler.sbptpicture.mongo.AsyncQueryBusinessPictureEntity;
import com.pangu.crawler.sbptpicture.utils.AesEncryptUtil;
import com.pangu.crawler.sbptpicture.utils.Base64ToFile;
import com.pangu.crawler.sbptpicture.utils.SymmetricEncoder;
import com.pangu.crawler.transfer.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
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

            if(StringUtils.isNotEmpty(request.getParameter("lsh"))){
                map.put("lsh",request.getParameter("lsh"));
            }
            if(StringUtils.isNotEmpty(request.getParameter("releationid"))){
                map.put("releationid",request.getParameter("releationid"));
            }
            if(StringUtils.isNotEmpty(request.getParameter("id"))){
                map.put("id",request.getParameter("id"));
            }
            List<AsyncQueryBusinessPictureEntity> data = reportService.queryHistoricalData(map);
            String message = "成功";
            if(data.size()==1){
                result.put("code","success");
                result.put("data",data.get(0));
            }else if(data.size()>1) {
                result.put("code","fail");
                result.put("data",data.get(0));
                message = "对应条件查询到"+data.size()+"条数据!";
            }else if(data.size()>1) {
                result.put("code","fail");
                result.put("data",null);
                message = "对应条件查询到"+data.size()+"条数据!";
            }
            result.put("message",message);
        }catch (Exception e){
            log.error("查询错误!");
        }finally {
            return  result;
        }
    }

    /***
     * 保存申报结果图片
     */
    @PostMapping
    @ResponseBody
    @RequestMapping("/viewqueryreportcation")
    public JSONObject viewqueryreportcation(HttpServletRequest request) throws Exception {
        Map<String,Object> result = new HashMap<String,Object>(3);
        JSONObject obj = new JSONObject();
        try{
            Map<String,String> map = new HashMap<String,String>(7);

            if(StringUtils.isNotEmpty(request.getParameter("jglx"))){
                map.put("jglx",request.getParameter("jglx"));
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
            AsyncQueryBusinessPictureEntity entity = null;
            List<AsyncQueryBusinessPictureEntity> data = reportService.queryHistoricalData(map);
            String message = "成功";
            if(data.size()==1){
                entity = data.get(0);
                obj.put("code","success");
                result.put("data",data.get(0));
            }else if(data.size()>1) {
                entity = data.get(0);
                obj.put("code","success");
                result.put("data",data.get(0));
                message = "对应条件查询到"+data.size()+"条数据!";
            }else{
                obj.put("code","fail");
                result.put("data",null);
                message = "对应条件查询到"+data.size()+"条数据!";
            }

            JSONObject doc = new JSONObject();
            JSONObject resultObj = new JSONObject();
            if(entity!=null){
                entity = data.get(0);
                resultObj.put("ip",entity.getIp());
                resultObj.put("sz",entity.getSz());
                resultObj.put("name",entity.getName());
                resultObj.put("nsrsbh",entity.getNsrsbh());
                resultObj.put("business",entity.getBusiness());
                resultObj.put("computername",entity.getComputername());
                resultObj.put("screen",entity.getScreenbase64());
                obj.put("message",message);
                result.put("data",resultObj);
            }
            doc.put("result",result.get("data"));
            obj.put("doc",doc);
            return obj;
        }catch (Exception e){
            result = new HashMap<String,Object>(3);
            obj.put("code","fail");
            obj.put("message","查询错误!");
            log.error("查询错误!");
            return  obj;
        }finally {
        }
    }

    @ResponseBody
    @PostMapping("/savereportcation")
    public Map<String,Object> savereportcation(HttpServletRequest request) throws Exception {//@RequestParam(value = "file",required=false)  MultipartFile file,
        Map<String,Object> result = new HashMap<String,Object>(2);
        try{
            MultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
            MultipartHttpServletRequest multipartRequest = resolver.resolveMultipart(request);
            MultipartFile file  = multipartRequest.getFile("file");
            String name = "";
            String  screenbase64 = request.getParameter("screenbase64");
           if(file!=null){
                name = file.getOriginalFilename();
                try{
                    screenbase64 = Base64ToFile.get(file.getInputStream()).replace("\r\n","");
                }catch (Exception e){
                    screenbase64 = "   ";
                }
            }
            String ip = request.getParameter("ip");
            String sz = request.getParameter("sz");
            String jglx = request.getParameter("jglx");
            String lsh = request.getParameter("lsh");
            String nsrsbh = request.getParameter("nsrsbh");
            String picname = request.getParameter("picname");

            String business = request.getParameter("business");
            String releationid = request.getParameter("releationid");
            String computername = request.getParameter("computername");
            Map<String,String> map = new HashMap<String,String>(7);
            if(StringUtils.isNotEmpty(ip)){
                map.put("ip",ip);
            }
            if(StringUtils.isNotEmpty(sz)){
                map.put("sz",sz);
            }
            if(StringUtils.isNotEmpty(lsh)){
                map.put("lsh",lsh);
            }
            if(StringUtils.isNotEmpty(jglx)){
                map.put("jglx",jglx);
            }
            if(StringUtils.isNotEmpty(name)){
                map.put("name",name);
            }
            if(StringUtils.isNotEmpty(nsrsbh)){
                map.put("nsrsbh",nsrsbh);
            }
            if(StringUtils.isNotEmpty(business)){
                map.put("business",business);
            }
            if(StringUtils.isNotEmpty(releationid)){
                map.put("releationid",releationid);
            }
            if(StringUtils.isNotEmpty(computername)){
                map.put("computername",computername);
            }
            if(StringUtils.isNotEmpty(screenbase64)){
                map.put("screenbase64", AesEncryptUtil.encrypt(screenbase64));
            }
            result =  reportService.savereportcation(map);
        }catch (Exception e){
            e.printStackTrace();
            result.put("code","fail");
            result.put("message",e.getMessage());
            log.error("保存申报图片失败!"+ TimeUtils.getCurrentDateTime(new Date(),TimeUtils.sdf2) +e.getMessage() );
        }finally {
            return result;
        }
    }

}
