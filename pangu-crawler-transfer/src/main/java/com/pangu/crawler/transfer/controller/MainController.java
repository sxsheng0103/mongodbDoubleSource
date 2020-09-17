package com.pangu.crawler.transfer.controller;

import com.pangu.crawler.framework.utils.StringUtils;
import com.pangu.crawler.transfer.service.CommonConfigService;
import com.pangu.crawler.transfer.utils.TempUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.schema.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author sheng.ding
 * @Date 2020/7/20 12:13
 * @Version 1.0
 **/
@Controller
@Slf4j
public class MainController {
    @Autowired
    CommonConfigService commonConfigService;
    @RequestMapping("/main")
    public ModelAndView main(ModelMap map){
        ModelAndView model = new ModelAndView("forward:/transfer/main.html");
        model.addObject("ss","sd22aad");
        map.put("ss","sd22aad");
        return model;
    }

}
