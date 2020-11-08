/*
package com.pangu.crawler.transfer.globalevent;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.web.servlet.View;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import java.io.IOException;
import java.util.Locale;
*/
/**
 * @Author sheng.ding
 * @Date 2020/7/26 14:27
 * @Version 1.0
 **//*

public class MyThymeleafViewResolver extends ThymeleafViewResolver {

    @Value("${spring.thymeleaf.prefix}")
    private String prefix;
    @Value("${spring.thymeleaf.suffix}")
    private String suffix;

    @Override
    protected View loadView(final String viewName, final Locale locale)throws Exception{
        String resourceName = prefix +viewName +suffix;
        try{
            //判断视图文件是否存在，如果不存在返回null
            this.getApplicationContext().getResource(resourceName).getInputStream();
        }catch (final IOException e){
            if(logger.isDebugEnabled()){
                if(logger.isTraceEnabled()){
                    logger.trace("视图名："+resourceName+"{}不存在");
                }else {
                    logger.trace("视图名："+resourceName+"{}不存在");
                }
            }
            return null;
        }
        return super.loadView(viewName,locale);
    }
}
*/
