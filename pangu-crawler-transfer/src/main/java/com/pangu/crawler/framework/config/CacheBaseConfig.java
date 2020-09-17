//package com.pangu.crawler.framework.config;
//
//public class CacheBaseConfig {
//
//    private static final int DEFAULT_TIMEOUT_OF_DATA_CACHE = 60;
//
//    private static final String DEFAULT_PASSWORD_FOR_DATA_CACHE = "0123456789abcdef";
//
//    private String timeoutOfDataCache;
//
//    private String passwordForDataCache;
//
//    public int getTimeoutOfDataCache() {
//        if (timeoutOfDataCache == null || timeoutOfDataCache.isEmpty()) {
//            return DEFAULT_TIMEOUT_OF_DATA_CACHE;
//        }
//        try {
//            return Integer.parseInt(timeoutOfDataCache);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void setTimeoutOfDataCache(String timeoutOfDataCache) {
//        this.timeoutOfDataCache = timeoutOfDataCache;
//    }
//
//    public String getPasswordForDataCache() {
//        if (passwordForDataCache == null || passwordForDataCache.isEmpty()) {
//            return DEFAULT_PASSWORD_FOR_DATA_CACHE;
//        }
//        return passwordForDataCache;
//    }
//
//    public void setPasswordForDataCache(String passwordForDataCache) {
//        this.passwordForDataCache = passwordForDataCache;
//    }
//
//    @Override
//    public String toString() {
//        return "timeoutOfDataCache='" + getTimeoutOfDataCache() + '\'' +
//                ", passwordForDataCache='" + getPasswordForDataCache() + '\'';
//    }
//}
