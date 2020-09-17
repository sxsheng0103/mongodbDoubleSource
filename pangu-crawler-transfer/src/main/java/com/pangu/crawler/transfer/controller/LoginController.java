package com.pangu.crawler.transfer.controller;

import com.pangu.crawler.framework.utils.StringUtils;
import com.pangu.crawler.transfer.utils.TempUserInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
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
public class LoginController {
    @ResponseBody
    @PostMapping("/loginCheckDefaultUser")
    public Map<String,Object> queryDataIndex(HttpServletRequest request, HttpServletResponse response)throws Exception{
        Map<String,Object> result = new HashMap<String,Object>(2);
        try{
            String account = request.getParameter("account");
            String password = request.getParameter("password");
            if(StringUtils.isNotEmpty(account)||StringUtils.isNotEmpty(account)){
                if(account.equals(TempUserInfo.user)&&password.equals(TempUserInfo.pass)){
                    result.put("code","success");
                    result.put("cookie","Secrit-Key");
                    result.put("age",TempUserInfo.maxAge);
                    result.put("Svalue",TempUserInfo.instanceCookieValue());
                    return result;
                }else{
                    result.put("code","fail");
                    result.put("message","用户名或密码错误!");
                }
            }else{
                result.put("code","fail");
                result.put("message","用户名或密码为空!");
            }
        }catch (Exception e){
            result.put("code","fail");
            result.put("message","登陆验证发生异常，请联系管理员!");
            e.printStackTrace();
        }finally {
            return result;
        }
    }

    @GetMapping("/timeouterror")
    public ModelAndView timeouterror(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new ModelAndView("redirect:/error.html");
        mv.addObject("message","安全验证超时，请重新!");
        return mv;
    }

    @GetMapping("/rejectProtocol")
    public ModelAndView rejectProtocol(HttpServletRequest request, HttpServletResponse response){
        ModelAndView mv = new ModelAndView("redirect:/timeouterror.html");
        mv.addObject("message","请使用https协议访问!");
        return mv;
    }

}
