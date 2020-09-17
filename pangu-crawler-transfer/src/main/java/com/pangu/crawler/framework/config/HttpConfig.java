//package com.pangu.crawler.framework.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//@Component
//@ConfigurationProperties(prefix = "pangu.http")
//public class HttpConfig implements InitializingBean {
//
//    private static final Logger logger = LoggerFactory.getLogger(HttpConfig.class);
//
//    private static final int DEFAULT_CONNECT_TIMEOUT = 30;
//
//    private static final int DEFAULT_READ_TIMEOUT = 60;
//
//    private String connectTimeout;
//
//    private String readTimeout;
//
//    public int getConnectTimeout() {
//        if (connectTimeout == null || connectTimeout.isEmpty()) {
//            return DEFAULT_CONNECT_TIMEOUT;
//        }
//        try {
//            return Integer.parseInt(connectTimeout);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void setConnectTimeout(String connectTimeout) {
//        this.connectTimeout = connectTimeout;
//    }
//
//    public int getReadTimeout() {
//        if (readTimeout == null || readTimeout.isEmpty()) {
//            return DEFAULT_READ_TIMEOUT;
//        }
//        try {
//            return Integer.parseInt(readTimeout);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void setReadTimeout(String readTimeout) {
//        this.readTimeout = readTimeout;
//    }
//
//    @Override
//    public String toString() {
//        return "HttpConfig{" +
//                "connectTimeout=" + getConnectTimeout() +
//                ", readTimeout=" + getReadTimeout() +
//                '}';
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        logger.info("{}", this);
//    }
//}
