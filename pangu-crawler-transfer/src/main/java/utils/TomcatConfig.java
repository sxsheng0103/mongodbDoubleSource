/*
package com.pangu.crawler.transfer.globalevent;

import org.apache.catalina.Context;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

*/
/**
 * @Author sheng.ding
 * @Date 2020/11/5 21:46
 * @Version 1.0
 **//*

@Configuration
public class TomcatConfig {
    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcatServletContainerFactory = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint constraint = new SecurityConstraint();
                constraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                collection.addPattern("/ywyydsj/*");
                collection.addMethod("HEAD");
                collection.addMethod("PUT");
                collection.addMethod("PATCH");
                collection.addMethod("DELETE");
                collection.addMethod("OPTIONS");
                collection.addMethod("TRACE");
                collection.addMethod("COPY");
                collection.addMethod("SEARCH");
                collection.addMethod("PROPFIND");
                constraint.addCollection(collection);
                constraint.setAuthConstraint(true);
                context.addConstraint(constraint);
                context.setUseHttpOnly(true);
                constraint.addCollection(collection);
                context.addConstraint(constraint);
            }
        };
        tomcatServletContainerFactory.addConnectorCustomizers(connector -> {
            connector.setAllowTrace(true);
        });
        return tomcatServletContainerFactory;
    }

}*/
