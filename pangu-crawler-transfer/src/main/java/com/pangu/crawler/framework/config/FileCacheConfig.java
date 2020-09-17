//package com.pangu.crawler.framework.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//@Component
//@ConfigurationProperties(prefix = "pangu.cache.file")
//public class FileCacheConfig extends CacheBaseConfig implements InitializingBean {
//
//    private static final Logger logger = LoggerFactory.getLogger(FileCacheConfig.class);
//
//    private static final String DEFAULT_PATH;
//
//    static {
//        DEFAULT_PATH = System.getProperty("user.home");
//    }
//
//    private String path;
//
//    public String getPath() {
//        if (path == null || path.isEmpty()) {
//            return DEFAULT_PATH;
//        }
//        return path;
//    }
//
//    public void setPath(String path) {
//        this.path = path;
//    }
//
//    @Override
//    public String toString() {
//        return "FileCacheConfig{" +
//                super.toString() +
//                ", path='" + getPath() + '\'' +
//                '}';
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        logger.info("{}", this);
//    }
//}
