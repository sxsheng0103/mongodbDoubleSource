/*
package com.pangu.crawler.transfer.globalevent;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;


*/
/**
 * @Author sheng.ding
 * @Date 2020/7/26 14:18
 * @Version 1.0
 **//*

@Configuration
@EnableAsync
public class MvcConfig extends WebMvcConfigurerAdapter {
    @Value("${spring.thymeleaf.template-resolver-order}")
    private int thymeleafTemplateResolverOrder;

    @Value("${spring.freemarker.template-resolver-order}")
    private int freemarkerTemplateResolverOrder;

    @Autowired
    private FreeMarkerViewResolver freeMarkerViewResolver;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

   @Bean
    public  FreeMarkerViewResolver freeMarkerViewResolverBean(){
        FreeMarkerViewResolver freeMarkerViewResolver2 = new FreeMarkerViewResolver();
        BeanUtils.copyProperties(freeMarkerViewResolver,freeMarkerViewResolver2);
        freeMarkerViewResolver2.setOrder(freemarkerTemplateResolverOrder);
        return freeMarkerViewResolver2;
    }

    @Bean
    public ThymeleafViewResolver thymeleafViewResolverBean(){
        MyThymeleafViewResolver thymeleafViewResolver2 = new MyThymeleafViewResolver();
        BeanUtils.copyProperties(thymeleafViewResolver,thymeleafViewResolver2);
        thymeleafViewResolver2.setOrder(thymeleafTemplateResolverOrder);
        return thymeleafViewResolver2;
    }
}

*/
