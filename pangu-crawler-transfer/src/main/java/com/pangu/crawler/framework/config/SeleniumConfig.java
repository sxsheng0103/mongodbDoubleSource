//package com.pangu.crawler.framework.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//@Component
//@ConfigurationProperties(prefix = "pangu.selenium")
//public class SeleniumConfig implements InitializingBean {
//
//    private static final Logger logger = LoggerFactory.getLogger(SeleniumConfig.class);
//
//    private static final int DEFAULT_SHORT_TIME = 3;
//
//    private static final int DEFAULT_LONG_TIME = 30;
//
//    private String shortTime;
//
//    private String longTime;
//
//    public int getShortTime() {
//        if (shortTime == null || shortTime.isEmpty()) {
//            return DEFAULT_SHORT_TIME;
//        }
//        try {
//            return Integer.parseInt(shortTime);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void setShortTime(String shortTime) {
//        this.shortTime = shortTime;
//    }
//
//    public int getLongTime() {
//        if (longTime == null || longTime.isEmpty()) {
//            return DEFAULT_LONG_TIME;
//        }
//        try {
//            return Integer.parseInt(longTime);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void setLongTime(String longTime) {
//        this.longTime = longTime;
//    }
//
//    @Override
//    public String toString() {
//        return "SeleniumConfig{" +
//                "shortTime=" + getShortTime() +
//                ", longTime=" + getLongTime() +
//                '}';
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        logger.info("{}", this);
//    }
//}
