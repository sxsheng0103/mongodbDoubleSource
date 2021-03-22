package com.fileupload.yzmslide.cache;

import org.apache.log4j.Logger;

public class MemoryCache extends AbstractCache implements ICache {

    private static final Logger logger = Logger.getLogger(MemoryCache.class);

    @Override
    protected CacheGetResult getCache(String invoker, String djxh, String skssqq, String skssqz, String currentPage, String pageSize) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void setCache(String invoker, String djxh, String skssqq, String skssqz, String currentPage, String pageSize, String cacheData, CacheTimeNumEnum timeNum, CacheTimeUnitEnum timeUnit) {
        // TODO Auto-generated method stub
    }

    @Override
    protected boolean clearCache(String invoker, String djxh, String skssqq, String skssqz, String currentPage, String pageSize) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected boolean deleteCache(String invoker, String djxh, String skssqq, String skssqz, String currentPage, String pageSize) {
        // TODO Auto-generated method stub
        return false;
    }

    public static void main(String[] args) throws Exception {
    }

}
