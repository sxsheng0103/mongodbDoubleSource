package com.fileupload.iec.app.commons.cache;

public interface ICache {

    CacheGetResult getCache(String invoker, String djxh, String skssqq, String skssqz, int currentPage, int pageSize);

    CacheGetResult getCache(String invoker, String djxh, String skssqq, String skssqz);

    CacheGetResult getCache(String invoker, String djxh);

    void setCache(String invoker, String djxh, String skssqq, String skssqz, int currentPage, int pageSize, String cacheData, CacheTimeNumEnum timeNum, CacheTimeUnitEnum timeUnit);

    void setCache(String invoker, String djxh, String skssqq, String skssqz, String cacheData, CacheTimeNumEnum timeNum, CacheTimeUnitEnum timeUnit);

    void setCache(String invoker, String djxh, String cacheData, CacheTimeNumEnum timeNum, CacheTimeUnitEnum timeUnit);

    boolean clearCache(String invoker, String djxh, String skssqq, String skssqz, int currentPage, int pageSize);

    boolean clearCache(String invoker, String djxh, String skssqq, String skssqz);

    boolean clearCache(String invoker, String djxh);

    boolean deleteCache(String invoker, String djxh, String skssqq, String skssqz, int currentPage, int pageSize);

    boolean deleteCache(String invoker, String djxh, String skssqq, String skssqz);

    boolean deleteCache(String invoker, String djxh);

    CacheGetResult getCache(ICacheKeyCombine cacheKeyCombine, ICacheBalanceValue cacheBalanceValue);

    void setCache(ICacheKeyCombine cacheKeyCombine, ICacheBalanceValue cacheBalanceValue, String cacheData, CacheTimeNumEnum timeNum, CacheTimeUnitEnum timeUnit);

    boolean clearCache(ICacheKeyCombine cacheKeyCombine, ICacheBalanceValue cacheBalanceValue);

    boolean deleteCache(ICacheKeyCombine cacheKeyCombine, ICacheBalanceValue cacheBalanceValue);
}