package com.fileupload.iec.app.commons.cache;

import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

abstract class AbstractCache implements ICache {

    private static final Logger logger = Logger.getLogger(AbstractCache.class);

    /**
     * 缓存主键（对于文件缓存就是缓存文件名）使用的分隔符。
     */
    private static final String CACHE_KEY_SEP = "_";

    /**
     * 添加到工作线程容器。
     *
     * @param cacheThreadMap
     * @param key
     * @param thread
     * @param logPrefix
     */
    protected static void addCacheThread(ConcurrentHashMap<String, Set<Thread>> cacheThreadMap, String key, Thread thread, String logPrefix) {
        if (cacheThreadMap == null) {
            logger.info(logPrefix + "<t:" + System.currentTimeMillis() + "> " + "add to cacheThreadMap error! cacheThreadMap is null!");
            return;
        }
        cacheThreadMap.putIfAbsent(key, new HashSet<Thread>());
        logger.info(logPrefix + "<t:" + System.currentTimeMillis() + "> " + "add to cacheThreadMap start! key = " + key + ", thread = " + thread + ", current = " + System.currentTimeMillis());
        boolean addResult = false;
        synchronized (cacheThreadMap) {
            addResult = cacheThreadMap.get(key).add(thread);
        }
        logger.info(logPrefix + "<t:" + System.currentTimeMillis() + "> " + "add to cacheThreadMap end! key = " + key + ", thread = " + thread + ", addResult = " + addResult + ", current = " + System.currentTimeMillis());
    }

    /**
     * 从工作线程容器中移除线程。
     *
     * @param cacheThreadMap
     * @param key
     * @param thread
     * @param logPrefix
     */
    protected static void removeCacheThread(ConcurrentHashMap<String, Set<Thread>> cacheThreadMap, String key, Thread thread, String logPrefix) {
        if (cacheThreadMap == null) {
            logger.info(logPrefix + "<t:" + System.currentTimeMillis() + "> " + "remove from cacheThreadMap error! cacheThreadMap is null!");
            return;
        }
        cacheThreadMap.putIfAbsent(key, new HashSet<Thread>());
        logger.info(logPrefix + "<t:" + System.currentTimeMillis() + "> " + "remove from cacheThreadMap start! key = " + key + ", thread = " + thread + ", current = " + System.currentTimeMillis());
        boolean removeResult = false;
        synchronized (cacheThreadMap) {
            removeResult = cacheThreadMap.get(key).remove(thread);
        }
        logger.info(logPrefix + "<t:" + System.currentTimeMillis() + "> " + "remove from cacheThreadMap end! key = " + key + ", thread = " + thread + ", removeResult = " + removeResult + ", current = " + System.currentTimeMillis());
    }

    /**
     * 移除并返回工作线程。
     *
     * @param cacheThreadMap
     * @param key
     * @param logPrefix
     * @return
     */
    protected static Set<Thread> removeAndGetCacheThreads(ConcurrentHashMap<String, Set<Thread>> cacheThreadMap, String key, String logPrefix) {
        if (cacheThreadMap == null) {
            logger.info(logPrefix + "<t:" + System.currentTimeMillis() + "> " + "remove and get cacheThreads from cacheThreadMap error! cacheThreadMap is null!");
            return null;
        }
        cacheThreadMap.putIfAbsent(key, new HashSet<Thread>());
        logger.info(logPrefix + "<t:" + System.currentTimeMillis() + "> " + "remove and get cacheThreads from cacheThreadMap start! key = " + key + ", current = " + System.currentTimeMillis());
        Set<Thread> result = new HashSet<Thread>();
        synchronized (cacheThreadMap) {
            Set<Thread> cacheThreads = cacheThreadMap.get(key);
            logger.info(logPrefix + "<t:" + System.currentTimeMillis() + "> " + "cacheThreadMap.get(" + key + ") = " + cacheThreads);
            while (!cacheThreads.isEmpty()) {
                logger.info(logPrefix + "<t:" + System.currentTimeMillis() + "> " + "cacheThreadMap.get(" + key + ") is not empty! value size = " + cacheThreads.size());
                Object[] threadArray = cacheThreads.toArray();
                for (int i = 0; i < threadArray.length; i++) {
                    if (threadArray[i] instanceof Thread) {
                        Thread thread = (Thread) threadArray[i];
                        boolean removeResult = cacheThreads.remove(thread);
                        if (removeResult) {
                            logger.info(logPrefix + "<t:" + System.currentTimeMillis() + "> " + "remove one from cacheThreadMap.get(" + key + ") success, then add this one to result! cacheThreadMap.get(" + key + ")[" + i + "] = " + thread);
                            result.add(thread);
                        } else {
                            logger.info(logPrefix + "<t:" + System.currentTimeMillis() + "> " + "remove one from cacheThreadMap.get(" + key + ") fail, so dont add this one to result! cacheThreadMap.get(" + key + ")[" + i + "] = " + thread);
                        }
                    } else {
                        logger.info(logPrefix + "<t:" + System.currentTimeMillis() + "> " + "remove one from cacheThreadMap.get(" + key + ") but type error, so dont add this one to result! cacheThreadMap.get(" + key + ")[" + i + "] = " + threadArray[i]);
                    }
                }
                cacheThreads = cacheThreadMap.get(key);
            }
        }
        logger.info(logPrefix + "<t:" + System.currentTimeMillis() + "> " + "remove and get cacheThreads from cacheThreadMap end! key = " + key + ", result = " + result + ", current = " + System.currentTimeMillis());
        return result;
    }

    /**
     * 合并字节数组（如果参数都是NULL，那么返回NULL；如果其中一个为NULL，那么直接返回另一个；如果都不是NULL，那么返回合并的数组）。
     *
     * @param bytes0
     * @param bytes1
     * @return
     */
    protected byte[] mergeBytes(byte[] bytes0, byte[] bytes1) {
        if (bytes0 == null && bytes1 == null) {
            return null;
        }
        if (bytes0 != null && bytes1 == null) {
            return bytes0;
        }
        if (bytes0 == null && bytes1 != null) {
            return bytes1;
        }
        byte[] result = new byte[bytes0.length + bytes1.length];
        System.arraycopy(bytes0, 0, result, 0, bytes0.length);
        System.arraycopy(bytes1, 0, result, bytes0.length, bytes1.length);
        return result;
    }

    abstract protected CacheGetResult getCache(String invoker, String djxh, String skssqq, String skssqz, String currentPage, String pageSize);

    abstract protected void setCache(String invoker, String djxh, String skssqq, String skssqz, String currentPage, String pageSize, String cacheData, CacheTimeNumEnum timeNum, CacheTimeUnitEnum timeUnit);

    abstract protected boolean clearCache(String invoker, String djxh, String skssqq, String skssqz, String currentPage, String pageSize);

    abstract protected boolean deleteCache(String invoker, String djxh, String skssqq, String skssqz, String currentPage, String pageSize);

    @Override
    public final CacheGetResult getCache(String invoker, String djxh, String skssqq, String skssqz, int currentPage, int pageSize) {
        return getCache(invoker, CACHE_KEY_SEP + djxh, CACHE_KEY_SEP + skssqq, CACHE_KEY_SEP + skssqz, CACHE_KEY_SEP + currentPage, CACHE_KEY_SEP + pageSize);
    }

    @Override
    public final CacheGetResult getCache(String invoker, String djxh, String skssqq, String skssqz) {
        return getCache(invoker, CACHE_KEY_SEP + djxh, CACHE_KEY_SEP + skssqq, CACHE_KEY_SEP + skssqz, null, null);
    }

    @Override
    public final CacheGetResult getCache(String invoker, String djxh) {
        return getCache(invoker, CACHE_KEY_SEP + djxh, CACHE_KEY_SEP + "X", CACHE_KEY_SEP + "Y", null, null);
    }

    @Override
    public final void setCache(String invoker, String djxh, String skssqq, String skssqz, int currentPage, int pageSize, String cacheData, CacheTimeNumEnum timeNum, CacheTimeUnitEnum timeUnit) {
        setCache(invoker, CACHE_KEY_SEP + djxh, CACHE_KEY_SEP + skssqq, CACHE_KEY_SEP + skssqz, CACHE_KEY_SEP + currentPage, CACHE_KEY_SEP + pageSize, cacheData, timeNum, timeUnit);
    }

    @Override
    public final void setCache(String invoker, String djxh, String skssqq, String skssqz, String cacheData, CacheTimeNumEnum timeNum, CacheTimeUnitEnum timeUnit) {
        setCache(invoker, CACHE_KEY_SEP + djxh, CACHE_KEY_SEP + skssqq, CACHE_KEY_SEP + skssqz, null, null, cacheData, timeNum, timeUnit);
    }

    @Override
    public final void setCache(String invoker, String djxh, String cacheData, CacheTimeNumEnum timeNum, CacheTimeUnitEnum timeUnit) {
        setCache(invoker, CACHE_KEY_SEP + djxh, CACHE_KEY_SEP + "X", CACHE_KEY_SEP + "Y", null, null, cacheData, timeNum, timeUnit);
    }

    @Override
    public final boolean clearCache(String invoker, String djxh, String skssqq, String skssqz, int currentPage, int pageSize) {
        return clearCache(invoker, CACHE_KEY_SEP + djxh, CACHE_KEY_SEP + skssqq, CACHE_KEY_SEP + skssqz, CACHE_KEY_SEP + currentPage, CACHE_KEY_SEP + pageSize);
    }

    @Override
    public final boolean clearCache(String invoker, String djxh, String skssqq, String skssqz) {
        return clearCache(invoker, CACHE_KEY_SEP + djxh, CACHE_KEY_SEP + skssqq, CACHE_KEY_SEP + skssqz, null, null);
    }

    @Override
    public final boolean clearCache(String invoker, String djxh) {
        return clearCache(invoker, CACHE_KEY_SEP + djxh, CACHE_KEY_SEP + "X", CACHE_KEY_SEP + "Y", null, null);
    }

    @Override
    public final boolean deleteCache(String invoker, String djxh, String skssqq, String skssqz, int currentPage, int pageSize) {
        return deleteCache(invoker, CACHE_KEY_SEP + djxh, CACHE_KEY_SEP + skssqq, CACHE_KEY_SEP + skssqz, CACHE_KEY_SEP + currentPage, CACHE_KEY_SEP + pageSize);
    }

    @Override
    public final boolean deleteCache(String invoker, String djxh, String skssqq, String skssqz) {
        return deleteCache(invoker, CACHE_KEY_SEP + djxh, CACHE_KEY_SEP + skssqq, CACHE_KEY_SEP + skssqz, null, null);
    }

    @Override
    public final boolean deleteCache(String invoker, String djxh) {
        return deleteCache(invoker, CACHE_KEY_SEP + djxh, CACHE_KEY_SEP + "X", CACHE_KEY_SEP + "Y", null, null);
    }

    @Override
    @Deprecated
    public final CacheGetResult getCache(ICacheKeyCombine cacheKeyCombine, ICacheBalanceValue cacheBalanceValue) {
        throw new RuntimeException("not support!");
    }

    @Override
    @Deprecated
    public final void setCache(ICacheKeyCombine cacheKeyCombine, ICacheBalanceValue cacheBalanceValue, String cacheData, CacheTimeNumEnum timeNum, CacheTimeUnitEnum timeUnit) {
        throw new RuntimeException("not support!");
    }

    @Override
    @Deprecated
    public final boolean clearCache(ICacheKeyCombine cacheKeyCombine, ICacheBalanceValue cacheBalanceValue) {
        throw new RuntimeException("not support!");
    }

    @Override
    @Deprecated
    public final boolean deleteCache(ICacheKeyCombine cacheKeyCombine, ICacheBalanceValue cacheBalanceValue) {
        throw new RuntimeException("not support!");
    }
}
