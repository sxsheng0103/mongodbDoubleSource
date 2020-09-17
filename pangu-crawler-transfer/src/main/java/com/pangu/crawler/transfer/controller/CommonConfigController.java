package com.pangu.crawler.transfer.controller;

import com.pangu.crawler.transfer.service.CommonConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Author sheng.ding
 * @Date 2020/7/20 12:13
 * @Version 1.0
 **/
@RequestMapping("/common")
@Controller
@Slf4j
public class CommonConfigController {

    @Autowired
    CommonConfigService commonConfigService;

    @ResponseBody
    @PostMapping ("querysz1")
    public Map querysz() throws Exception {
        return null;
    }
    @ResponseBody
    @PostMapping("/getsz")
    public Map<String,String> getsz(ModelMap map){
        try {
            return commonConfigService.getSzMap("");
        }catch (Exception e){
            log.error("税种获取失败");
            return null;
        }
    }
    @ResponseBody
    @PostMapping("/getdq")
    public Map<String,String> getdq(ModelMap map){
        try {
            return commonConfigService.getDqMap("");
        }catch (Exception e){
            log.error("地区获取失败");
            return null;
        }
    }


    @ResponseBody
    @PostMapping("/getFormsBysz")
    public Map<String,String> getFormsBysz(String szcode){
        try {
            return commonConfigService.getFormsBysz(szcode);
        }catch (Exception e){
            log.error("按税种获取表单");
            return null;
        }
    }

}
