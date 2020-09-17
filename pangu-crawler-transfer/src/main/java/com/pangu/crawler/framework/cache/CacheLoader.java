//package com.pangu.crawler.framework.cache;
//
//import javax.validation.constraints.NotNull;
//
//public interface CacheLoader {
//
//    default String urlSuffix() {
//        return "/cache/{key}";
//    }
//
//    String load(@NotNull String trace, @NotNull String key) throws CacheTimeoutException;
//
//    boolean save(@NotNull String trace, @NotNull String key, @NotNull String value);
//}
