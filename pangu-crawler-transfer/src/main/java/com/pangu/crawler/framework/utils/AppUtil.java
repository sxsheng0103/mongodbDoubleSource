package com.pangu.crawler.framework.utils;

import com.pangu.crawler.business.controller.AsyncQueryInterface;
import com.pangu.crawler.business.controller.ChromeController;
import com.pangu.crawler.business.controller.IBaseController;
import com.pangu.crawler.business.controller.ShenbaoInterface;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: pc
 * @Date: 2019/12/20 10:00
 * @Description:
 */
@Component
public class AppUtil implements ApplicationContextAware {
    /**
     * 查询类
     */
    private Map<String, IBaseController> baseControllerMap = new HashMap();
    
    private Map<String, ChromeController> chromeControllerMap = new HashMap();
    
    private Map<String, AsyncQueryInterface> asyncQueryInterfaceMap = new HashMap();
    
    private Map<String, ShenbaoInterface> shenbaoInterfaceMap = new HashMap();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        baseControllerMap = applicationContext.getBeansOfType(IBaseController.class);
        chromeControllerMap = applicationContext.getBeansOfType(ChromeController.class);
        asyncQueryInterfaceMap=applicationContext.getBeansOfType(AsyncQueryInterface.class);
        shenbaoInterfaceMap=applicationContext.getBeansOfType(ShenbaoInterface.class);
    }

    public IBaseController getBaseController(String serviceName) {
        return baseControllerMap.get(serviceName);
    }
    
    public ChromeController getChromeController(String serviceName) {
        return chromeControllerMap.get(serviceName);
    }
    public AsyncQueryInterface getAsyncQueryInterface(String serviceName) {
        return asyncQueryInterfaceMap.get(serviceName);
    }
    
    public ShenbaoInterface getShenbaoInterface(String serviceName) {
        return shenbaoInterfaceMap.get(serviceName);
    }
    
    public boolean containsAsyncQueryInterface(String serviceName) {
        return asyncQueryInterfaceMap.containsKey(serviceName);
    }

}
