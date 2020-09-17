//package com.pangu.crawler.framework.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//@Component
//@ConfigurationProperties(prefix = "pangu.cache.http")
//public class HttpCacheConfig extends CacheBaseConfig implements InitializingBean {
//
//    private static final Logger logger = LoggerFactory.getLogger(HttpCacheConfig.class);
//
//    private static final String DEFAULT_HOST = "http://127.0.0.1:8080";
//
//    private String host;
//
//    public String getHost() {
//        if (host == null || host.isEmpty()) {
//            return DEFAULT_HOST;
//        }
//        return host;
//    }
//
//    public void setHost(String host) {
//        this.host = host;
//    }
//
//    @Override
//    public String toString() {
//        return "HttpCacheConfig{" +
//                super.toString() +
//                ", host='" + getHost() + '\'' +
//                '}';
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        logger.info("{}", this);
//    }
//}
