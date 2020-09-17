//package com.pangu.crawler.framework.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//@Component
//@ConfigurationProperties(prefix = "pangu.cookie")
//public class CookieConfig implements InitializingBean {
//
//    private static final Logger logger = LoggerFactory.getLogger(CookieConfig.class);
//
//    private static final boolean DEFAULT_REFRESH_ENABLE = true;
//
//    private static final int DEFAULT_REFRESH_INTERNAL = 1;
//
//    private String refreshInternal;
//
//    private String refreshEnable;
//
//    public int getRefreshInternal() {
//        if (refreshInternal == null || refreshInternal.isEmpty()) {
//            return DEFAULT_REFRESH_INTERNAL;
//        }
//        try {
//            return Integer.parseInt(refreshInternal);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void setRefreshInternal(String refreshInternal) {
//        this.refreshInternal = refreshInternal;
//    }
//
//    public boolean isRefreshEnable() {
//        if (refreshEnable == null || refreshEnable.isEmpty()) {
//            return DEFAULT_REFRESH_ENABLE;
//        }
//        try {
//            return Boolean.parseBoolean(refreshEnable);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void setRefreshEnable(String refreshEnable) {
//        this.refreshEnable = refreshEnable;
//    }
//
//    @Override
//    public String toString() {
//        return "CookieConfig{" +
//                "refreshEnable=" + isRefreshEnable() +
//                ", refreshInternal=" + getRefreshInternal() +
//                '}';
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        logger.info("{}", this);
//    }
//}
