package com.pangu.crawler.framework.aspect;

import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.framework.cookie.*;
import com.pangu.crawler.framework.exception.LoginExpiredException;
import com.pangu.crawler.framework.exception.PanicException;
/*import com.pangu.crawler.framework.http.HttpManager;
import com.pangu.crawler.framework.service.ServiceFirstArg;*/
import com.pangu.crawler.framework.utils.Base64Util;
import com.pangu.crawler.transfer.utils.TempUserInfo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Stream;

@Aspect
@Component
public class Aspects implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(Aspects.class);

    private ApplicationContext applicationContext;

/*    @Autowired
    private HttpManager httpManager;*/

    /*@Autowired
    private CookieLoader cookieLoader;
*/
    /*@Autowired
    private CookieFreshTimer cookieFreshTimer;*/

/*    @Autowired(required = false)
    private CookieExpiredChecker cookieExpiredChecker;*/

    private static boolean test(Cookie e) {
        if (e.getName() != null && e.getName().equals("Secrit-Key") && e.getValue().equals(TempUserInfo.cookieValue)) {
            return true;
        }else{
            return false;
        }
    }

    @Pointcut("execution(* com.pangu.crawler.transfer..*Controller.*(..))")
    private void controller() {
    }

    private void superTrimJsonXml(Object[] objects) {
        if (objects == null) {
            return;
        }
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] != null && objects[i] instanceof String) {
                String s = objects[i].toString().trim();
                if ((s.startsWith("{") && s.endsWith("}"))
                        || (s.startsWith("[") && s.endsWith("]"))
                        || (s.startsWith("<") && s.endsWith(">"))) {
                    objects[i] = s.replaceAll("\r|\n|\\s", "");
                }
            }
        }
    }

    @Before("controller()")
    public void beforeController(JoinPoint joinPoint) {
        /*Object target = joinPoint.getTarget();
        String signature = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        superTrimJsonXml(args);
        String execution = String.format("%s.%s(%s)", target, signature, Arrays.toString(args));

        logger.info("{} start!", execution);*/
        /*HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        try{
            String protocol = request.getScheme();
            if (protocol==null||protocol.equals("http")) {
//                response.sendRedirect("rejectProtocol");
                request.getRequestDispatcher("rejectProtocol").forward(request, response);
                return;
            }
        }catch (Exception e){
            throw new RuntimeException("请使用https协议访问！");
        }*/
    }

    @AfterReturning(pointcut = "controller()", returning = "result")
    public void afterControllerReturn(JoinPoint joinPoint, Object result) {
    	/*if(!(result instanceof String)) {
    		CookieManager.clearCookies();
    		return;
    	}
    	
        Object target = joinPoint.getTarget();
        String signature = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        superTrimJsonXml(args);
        String execution = String.format("%s.%s(%s)", target, signature, Arrays.toString(args));
        CookieManager.clearCookies();
        logger.info("[{}] - clear cookies success after {} return!", result, execution);
        JSONObject resultJsonObject;
        try {
            resultJsonObject = JSONObject.parseObject(String.valueOf(result));
        } catch (Exception e) {
            throw new RuntimeException("[" + execution + "] result parse object fail! result = " + result);
        }
        String trace = resultJsonObject.getString("trace");
        if (trace == null || trace.isEmpty()) {
            throw new RuntimeException("trace from [" + execution + "] result is empty! result = " + result);
        }
        String nsrsbh = resultJsonObject.getString("nsrsbh");
        if (nsrsbh == null || nsrsbh.isEmpty()) {
            throw new RuntimeException("nsrsbh from [" + execution + "] result is empty! result = " + result);
        }
        logger.info("{} end! result = {}", execution, result);*/
    }

    @AfterThrowing(pointcut = "controller()", throwing = "e")
    public void afterControllerException(JoinPoint joinPoint, Exception e) throws Exception {
        /*Object target = joinPoint.getTarget();
        String signature = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        superTrimJsonXml(args);
        String execution = String.format("%s.%s(%s)", target, signature, Arrays.toString(args));
//        if (!(e instanceof PanicException)) {
//            logger.error("fatal error occur!!! controller throw wrong exception type : {} when {}!!!",
//                    e.getClass(), execution, e);
//            System.exit(-1);
//        }
//        PanicException panic = (PanicException) e;
//        panic.setController(execution);
        CookieManager.clearCookies();
        logger.info("切面捕获到异常:"+execution+":",e);
//        logger.info("[{}] - clear cookies success after {} throw exception!", panic.getTrace(), execution);
//        logger.info("[{}] - {} throw exception!", panic.getTrace(), execution);
        throw e;*/
    }

    @Around("execution(* com.pangu.crawler.transfer..*Controller.*(..)) && !execution(* com.pangu.crawler.transfer.controller.LoginController.*(..))")// && !execution(* com.pangu.crawler.transfer.sbptpicture.service.ReportController.*(..))
    public Object aroundService(ProceedingJoinPoint joinPoint) throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        boolean valid =  false;
        Cookie[] cookies = request.getCookies();
        //E7oVBGYm&CxXOZUqpZ%&!FOXf#wLkhyqXY##*4#rYgoY#NafZw^OgMt^$5joZVfi
        //RTdvVkJHWW0mQ3hYT1pVcXBaJSYhRk9YZiN3TGtoeXFYWSMjKjQjcllnb1kjTmFmWndeT2dNdF4kNWpvWlZmaQ==
       try {
           if(request.getHeader("sbptmiyao")!=null&& Base64Util.decode(request.getHeader("sbptmiyao")).equals("E7oVBGYm&CxXOZUqpZ%&!FOXf#wLkhyqXY##*4#rYgoY#NafZw^OgMt^$5joZVfi")){
               return  joinPoint.proceed(joinPoint.getArgs());
           }
           if(cookies!=null){
               Optional<Cookie> cookie= Arrays.asList(cookies).stream().filter(e->e.getName() != null && e.getName().equals("Secrit-Key") && e.getValue().equals(TempUserInfo.cookieValue)).findFirst();

               if(cookie.isPresent()){
                    return  joinPoint.proceed(joinPoint.getArgs());
                }else{
                    response.sendRedirect("/timeouterror");
//                    request.getRequestDispatcher("/timeouterror").forward(request,response);
                    throw new RuntimeException("无权限或访问权限超时，请重新尝试！");
//                    response.setContentType("text/html");
                  /*  PrintWriter out = response.getWriter();
                    out.write("<html><head><title>Exception/Error Details</title></head><body>");
//                    if(statusCode != 500){
//                        out.write("<h3>Error Details</h3>");
//                        out.write("<strong>Status Code</strong>:"+statusCode+"<br>");
//                        out.write("<strong>Requested URI</strong>:"+requestUri);
//                    }else{
                        out.write("<h3>Exception Details</h3>");
                        out.write("<ul><li>Servlet Name:"+""+"</li>");
                        out.write("<li>Exception Name:"+"".getClass().getName()+"</li>");
                        out.write("<li>Requested URI:"+""+"</li>");
                        out.write("<li>Exception Message:</li>");
                        out.write("</ul>");
//                    }
                    out.write("<br><br>");
                    out.write("<a href=\"index.html\">Home Page</a>");
                    out.write("</body></html>");
                    response.sendRedirect(request.getContextPath());
//                    ModelAndView("redirect:/users")
//                    return "forward:error.html";
                    ModelAndView mv = new ModelAndView("redirect:/main.html");
//                    return "{\"updated_at\":1551780617}";
                    return new InternalResourceView("foward:/error.html");*/
                }
           }else{
               response.sendRedirect("/timeouterror");
               throw new RuntimeException("无权限或访问权限超时，请重新尝试！");
           }
       } catch (Throwable t) {
                // 输出异常日志
                String errorMessage = t.getMessage();
                logger.info(errorMessage);
                return null;
       }

        /*String signature = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        superTrimJsonXml(args);
        String execution = String.format("%s.%s(%s)", target, signature, Arrays.toString(args));
        // 判断第一个参数
        if (args == null || args.length <= 0) {
            logger.error("fatal error occur!!! service args is empty : {}", execution);
            System.exit(-1);
        }
        Object arg = args[0];
        if (!(arg instanceof ServiceFirstArg)) {
            logger.error("fatal error occur!!! service first arg error : {}!!!", execution);
            System.exit(-1);
        }
        ServiceFirstArg firstArg = (ServiceFirstArg) arg;
        String trace = firstArg.getTrace();
        String nsrsbh = firstArg.getNsrsbh();
        CookieOperation cookieOperation = firstArg.getCookieOperation();
        // 添加HttpManager对象
        if (firstArg.getHttpManager() == null) {
            firstArg.setHttpManager(httpManager);
        }
        // 输出开始日志
        logger.info("[{}] - {} start!", trace, execution);
        // 如果加载Cookie
        if (cookieOperation == CookieOperation.LOAD) {
            logger.info("[{}] - load cookies start! nsrsbh = {}", trace, nsrsbh);
            Map<CookieKey, List<String>> oldCookies = CookieManager.clearCookies();
            logger.info("[{}] - local cookies clear and old cookies tobe merge before load! nsrsbh = {}, old cookies = {}",
                    trace, nsrsbh, oldCookies);
            Map<CookieKey, List<String>> loadCookies = cookieLoader.loadCookies(trace, nsrsbh);
            if (logger.isDebugEnabled()) {
                logger.debug("[{}] - loaded cookies! nsrsbh = {}, load cookies = {}", trace, nsrsbh, loadCookies);
            }
            if (loadCookies == null || loadCookies.isEmpty()) {
                logger.info("[{}] - load cookies is empty! nsrsbh = {}", trace, nsrsbh);
                throw new LoginExpiredException(trace, nsrsbh);
            } else {
                final Map<CookieKey, List<String>> allCookies = new HashMap<>();
                if (oldCookies != null && oldCookies.size() > 0) {
                    // 加载的覆盖原来的。
                    oldCookies.forEach(allCookies::put);
                    loadCookies.forEach(allCookies::put);
                    logger.info("[{}] - merge old cookies after load! nsrsbh = {}, all cookies = {}",
                            trace, nsrsbh, allCookies);
                } else {
                    // 仅用加载的。
                    loadCookies.forEach(allCookies::put);
                    logger.info("[{}] - old cookies is empty, not merge after load! nsrsbh = {}, all cookies = {}",
                            trace, nsrsbh, allCookies);
                }
                allCookies.forEach(CookieManager::putCookie);
                logger.info("[{}] - load cookies success! nsrsbh = {}", trace, nsrsbh);
            }
        }
        // 判断Cookie是否过期
        if (cookieExpiredChecker != null) {
            if (firstArg.isCookieExpiredCheck()) {
                if (!(target instanceof CookieExpiredCheckerMarker)) {
                    if (!cookieExpiredChecker.preCheck(applicationContext, ServiceFirstArg.cookie(trace, nsrsbh))) {
                        throw new LoginExpiredException(trace, nsrsbh);
                    }
                }
            } else {
                logger.info("[{}] - do not cookie expired check! nsrsbh = {}", trace, nsrsbh);
            }
        } else {
            logger.info("[{}] - cookie expired checker not autowired! nsrsbh = {}", trace, nsrsbh);
        }
        // 业务逻辑执行
        Object result = null;
        try {
            result = joinPoint.proceed(joinPoint.getArgs());
        } catch (Throwable t) {
            // 输出异常日志
            String errorMessage = t.getMessage();
            if (t.getCause() != null) {
                if (errorMessage != null && !errorMessage.trim().isEmpty()) {
                    errorMessage += " -> " + t.getCause().getMessage();
                } else {
                    errorMessage = t.getCause().getMessage();
                }
            }
            logger.error("[{}] - {} throw exception! e = {}", trace, execution, errorMessage);
            if (t instanceof Exception) {
                throw (Exception) t;
            } else {
                logger.error("fatal error occur!!!", t);
                System.exit(-1);
            }
        }
        // 判断Cookie是否过期
        if (cookieExpiredChecker != null) {
            if (firstArg.isCookieExpiredCheck()) {
                if (!(target instanceof CookieExpiredCheckerMarker)) {
                    if (!cookieExpiredChecker.postCheck(applicationContext, ServiceFirstArg.cookie(trace, nsrsbh), result)) {
                        throw new LoginExpiredException(trace, nsrsbh);
                    }
                }
            } else {
                logger.info("[{}] - do not cookie expired check! nsrsbh = {}", trace, nsrsbh);
            }
        } else {
            logger.info("[{}] - cookie expired checker not autowired! nsrsbh = {}", trace, nsrsbh);
        }
        // 如果保存Cookie
        if (cookieOperation == CookieOperation.SAVE) {
            logger.info("[{}] - save cookies start! nsrsbh = {}", trace, nsrsbh);
            Map<CookieKey, List<String>> cookies = CookieManager.getCookieLists();
            if (logger.isDebugEnabled()) {
                logger.debug("[{}] - saved cookies! nsrsbh = {}, cookies = {}", trace, nsrsbh, cookies);
            }
            boolean saved = cookieLoader.saveCookies(trace, nsrsbh, cookies);
            logger.info("[{}] - clear local cookies after save cookies! nsrsbh = {}", trace, nsrsbh);
            CookieManager.clearCookies();
            if (!saved) {
                logger.info("[{}] - save cookies fail! nsrsbh = {}", trace, nsrsbh);
                throw new Exception("save cookies failed!");
            } else {
                logger.info("[{}] - save cookies success! nsrsbh = {}", trace, nsrsbh);
            }
        }
        logger.info("[{}] - add nsrsbh to cookie fresh timer! nsrsbh = {}", trace, nsrsbh);
        cookieFreshTimer.addNsrsbh(nsrsbh);
        // 输出结束日志
        logger.info("[{}] - {} return! result = {}", trace, execution, result);
        return result;*/
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
