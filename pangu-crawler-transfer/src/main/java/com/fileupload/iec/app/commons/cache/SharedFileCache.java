package com.fileupload.iec.app.commons.cache;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SharedFileCache implements ICache {

    private static final Logger logger = Logger.getLogger(SharedFileCache.class);

    /**
     * Properties文件中缓存写锁重试的间隔时长(单位:毫秒)的Key。
     */
    private static final String CACHE_LOCK_INTERVAL_KEY = "shared_file_cache_lock_interval";

    /**
     * 默认的缓存写锁重试的间隔时长(单位:毫秒)。
     */
    private static final int DEFAULT_CACHE_LOCK_INTERVAL = 10;

    /**
     * Properties文件中缓存写锁重试次数的Key。
     */
    private static final String CACHE_LOCK_RETRY_KEY = "shared_file_cache_lock_retry";

    /**
     * 默认的缓存写锁重试次数。
     */
    private static final int DEFAULT_CACHE_LOCK_RETRY = 50;

    /**
     * Properties文件中缓存目录列表的Key。
     */
    private static final String CACHE_DIR_PROP_KEY = "cache_dir";

    /**
     * 默认的缓存目录列表。
     */
    private static final String DEFAULT_CACHE_DIR = "D:\\cache\\cache\\000;D:\\cache\\cache\\001;D:\\cache\\cache\\002";

    /**
     * Properties文件中缓存目录列表的分隔符。
     */
    private static final String CACHE_DIR_PROP_VALUE_SEP = ";";

    /**
     * 缓存内容使用的分隔符。
     */
    private static final String CACHE_CONTENT_SEP = "###";

    /**
     * 文件锁目录（实际使用时追加在缓存目录列表各项后面）。
     */
    private static final String LOCK_DIR = "locks";

    /**
     * 从Properties文件中解析完成标识。
     */
    private static volatile boolean propertiesReaded = false;

    /**
     * 从Properties文件中解析得到的缓存目录列表。
     */
    private static List<String> cacheDirList = null;

    /**
     * 从Properties文件中解析得到的缓存写锁重试的间隔时长(单位:毫秒)。
     */
    private static int cacheLockInterval = 0;

    /**
     * 从Properties文件中解析得到的缓存写锁重试次数。
     */
    private static int cacheLockRetry = 0;

    /**
     * 均衡策略。
     */
    private static ICacheBalanceStrategy cacheBalanceStrategy = null;

    public static SharedFileCache getInstance() {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        if (!readProperties(uuid, true)) {
            return null;
        }
        return new SharedFileCache(uuid);
    }

    private String uuid;

    private SharedFileCache(String uuid) {
        this.uuid = uuid;
    }

    /**
     * 同步读取配置文件。
     *
     * @param uuid
     * @return
     */
    private static synchronized boolean readProperties(String uuid, boolean useDefault) {
        if (propertiesReaded) {
            return true;
        }
        long t = System.nanoTime();
        final String LOG_PREFIX = "readProperties[uuid=" + uuid + "] ";
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "start!");
        String cacheDirProp = null;
        if (useDefault) {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "use default!");
            cacheLockInterval = DEFAULT_CACHE_LOCK_INTERVAL;
            cacheLockRetry = DEFAULT_CACHE_LOCK_RETRY;
            cacheDirProp = DEFAULT_CACHE_DIR;
        } else {
            // 获取写锁重试的间隔时长。
            // String cacheLockIntervalProp = MinaLoadPrpo.getInstance().getValue(CACHE_LOCK_INTERVAL_KEY);
            String cacheLockIntervalProp = "";
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cacheLockIntervalProp read from properties! key = " + CACHE_LOCK_INTERVAL_KEY + ", cacheLockIntervalProp = " + cacheLockIntervalProp);
            try {
                cacheLockInterval = Integer.parseInt(cacheLockIntervalProp);
            } catch (Exception e) {
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cacheLockIntervalProp read from properties parseInt fail, use default value! dafault is " + DEFAULT_CACHE_LOCK_INTERVAL);
                cacheLockInterval = DEFAULT_CACHE_LOCK_INTERVAL;
            }
            // 获取写锁重试的次数。
            // String cacheLockRetryProp = MinaLoadPrpo.getInstance().getValue(CACHE_LOCK_RETRY_KEY);
            String cacheLockRetryProp = "";
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cacheLockRetryProp read from properties! key = " + CACHE_LOCK_RETRY_KEY + ", cacheLockRetryProp = " + cacheLockRetryProp);
            try {
                cacheLockRetry = Integer.parseInt(cacheLockRetryProp);
            } catch (Exception e) {
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cacheLockRetryProp read from properties parseInt fail, use default value! dafault is " + DEFAULT_CACHE_LOCK_RETRY);
                cacheLockRetry = DEFAULT_CACHE_LOCK_RETRY;
            }
            // 获取缓存目录。
            // cacheDirProp = MinaLoadPrpo.getInstance().getValue(CACHE_DIR_PROP_KEY);
            cacheDirProp = "";
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cacheDirProp read from properties! key = " + CACHE_DIR_PROP_KEY + ", cacheDirProp = " + cacheDirProp);
            if (cacheDirProp == null) {
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cacheDirProp read from properties is null, use default value! dafault is " + DEFAULT_CACHE_DIR);
                cacheDirProp = DEFAULT_CACHE_DIR;
            }
            if (cacheDirProp.isEmpty()) {
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cacheDirProp read from properties is empty, use default value! dafault is " + DEFAULT_CACHE_DIR);
                cacheDirProp = DEFAULT_CACHE_DIR;
            }
        }
        // 处理缓存目录及父目录，还有其中的文件锁目录。
        if (!processCacheAndLockDir(cacheDirProp, LOG_PREFIX, t)) {
            return false;
        }
        // 确定均衡策略。
        cacheBalanceStrategy = new ICacheBalanceStrategy() {
            private String[] repositories;

            @Override
            public void setBalanceRepositories(String[] repositories) {
                this.repositories = repositories;
            }

            @Override
            public String getBalanceRepository(int balanceValue) {
                if (repositories == null) {
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "getBalanceRepository error! repositories is null!");
                    return null;
                }
                if (repositories.length <= 0) {
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "getBalanceRepository error! repositories is empty!");
                    return null;
                }
                if (balanceValue < 0) {
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "getBalanceRepository error! balanceValue is error! balanceValue = " + balanceValue);
                    return null;
                }
                return this.repositories[balanceValue % this.repositories.length];
            }
        };
        String[] repositories = new String[cacheDirList.size()];
        cacheBalanceStrategy.setBalanceRepositories(cacheDirList.toArray(repositories));
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cost(ns) = " + (System.nanoTime() - t));
        propertiesReaded = true;
        return true;
    }

    /**
     * 处理缓存目录及父目录，还有其中的文件锁目录。
     *
     * @param cacheDirProp
     * @param logPrefix
     * @param t
     * @return
     */
    private static synchronized boolean processCacheAndLockDir(String cacheDirProp, String logPrefix, long t) {
        if (cacheDirProp == null) {
            logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "end! cacheDirProp is null! cost(ns) = " + (System.nanoTime() - t));
            return false;
        }
        if (cacheDirProp.isEmpty()) {
            logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "end! cacheDirProp is empty! cost(ns) = " + (System.nanoTime() - t));
            return false;
        }
        String[] cacheDirArray = cacheDirProp.split(CACHE_DIR_PROP_VALUE_SEP);
        logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "cacheDirProp read from properties, split by \"" + CACHE_DIR_PROP_VALUE_SEP + "\", cacheDirArray = " + ArrayUtils.toString(cacheDirArray));
        if (cacheDirArray == null) {
            logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "end! cacheDirArray is null! cost(ns) = " + (System.nanoTime() - t));
            return false;
        }
        if (cacheDirArray.length <= 0) {
            logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "end! cacheDirArray is empty! cost(ns) = " + (System.nanoTime() - t));
            return false;
        }
        // 依次创建本目录及上级目录。
        for (int i = 0; i < cacheDirArray.length; i++) {
            String cacheDir = cacheDirArray[i];
            logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "make dir and parent dir! cacheDirArray[" + i + "] = " + cacheDir);
            if (cacheDir == null) {
                logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "end! cacheDirArray[" + i + "] is null! cost(ns) = " + (System.nanoTime() - t));
                return false;
            }
            if (cacheDir.isEmpty()) {
                logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "end! cacheDirArray[" + i + "] is empty! cost(ns) = " + (System.nanoTime() - t));
                return false;
            }
            // 依次获取本目录及父目录对象保存到列表中。
            File cacheDirFile = new File(cacheDir);
            List<File> dirFileList = new ArrayList<File>();
            dirFileList.add(cacheDirFile);
            File parentDirFile = cacheDirFile.getParentFile();
            while (parentDirFile != null) {
                dirFileList.add(parentDirFile);
                parentDirFile = parentDirFile.getParentFile();
            }
            logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "cacheDirArray[" + i + "] dir and parent dir list getted! dirFileList = " + dirFileList);
            // 依次创建本目录及父目录对象，从最上层目录开始。
            File dirFile = null;
            try {
                for (int j = dirFileList.size() - 1; j >= 0; j--) {
                    dirFile = dirFileList.get(j);
                    if (!dirFile.exists()) {
                        logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "dir not exist! dirFile = " + dirFile);
                        boolean dirFileMkdirResult = dirFile.mkdir();
                        if (dirFileMkdirResult) {
                            logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "dir make success! dirFile = " + dirFile);
                        } else {
                            logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "end! dir make fail! dirFile = " + dirFile);
                            return false;
                        }
                    } else {
                        logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "dir exist! dirFile = " + dirFile);
                    }
                }
            } catch (Exception e) {
                logger.error(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "end! dir make error! dirFile = " + dirFile, e);
                return false;
            }
            // 本目录及父目录创建成功后，创建其下的文件锁目录。
            File lockDirFile = new File(getLockDir(cacheDir));
            try {
                if (!lockDirFile.exists()) {
                    logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "lock dir not exist! lockDirFile = " + lockDirFile);
                    boolean lockDirFileMkdirResult = lockDirFile.mkdir();
                    if (lockDirFileMkdirResult) {
                        logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "lock dir make success! lockDirFile = " + lockDirFile);
                    } else {
                        logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "end! lock dir make fail! lockDirFile = " + lockDirFile);
                        return false;
                    }
                } else {
                    logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "lock dir exist! lockDirFile = " + lockDirFile);
                }
            } catch (Exception e) {
                logger.error(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "end! dir make error! lockDirFile = " + lockDirFile, e);
                return false;
            }
        }
        // 执行成功后保存。
        List<String> cacheDirListLocal = new ArrayList<String>();
        for (int i = 0; i < cacheDirArray.length; i++) {
            cacheDirListLocal.add(cacheDirArray[i]);
        }
        cacheDirList = Collections.unmodifiableList(cacheDirListLocal);
        logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "cacheDirList = " + cacheDirList);
        return true;
    }

    /**
     * 根据缓存KEY取得缓存文件名。
     *
     * @param key
     * @return
     */
    private static String getCacheFileName(String key) {
        return key + ".cache";
    }

    /**
     * 根据缓存KEY取得缓存文件对应的锁文件名。
     *
     * @param key
     * @return
     */
    private static String getLockFileName(String key) {
        return key + ".lock";
    }

    /**
     * 根据缓存目录确定其对应的锁文件目录。
     *
     * @param cacheDir
     * @return
     */
    private static String getLockDir(String cacheDir) {
        return cacheDir + File.separator + LOCK_DIR;
    }

    @Override
    @Deprecated
    public final CacheGetResult getCache(String invoker, String djxh, String skssqq, String skssqz, int currentPage, int pageSize) {
        throw new RuntimeException("not support!");
    }

    @Override
    @Deprecated
    public final CacheGetResult getCache(String invoker, String djxh, String skssqq, String skssqz) {
        throw new RuntimeException("not support!");
    }

    @Override
    @Deprecated
    public final CacheGetResult getCache(String invoker, String djxh) {
        throw new RuntimeException("not support!");
    }

    @Override
    @Deprecated
    public final void setCache(String invoker, String djxh, String skssqq, String skssqz, int currentPage, int pageSize, String cacheData, CacheTimeNumEnum timeNum, CacheTimeUnitEnum timeUnit) {
        throw new RuntimeException("not support!");
    }

    @Override
    @Deprecated
    public final void setCache(String invoker, String djxh, String skssqq, String skssqz, String cacheData, CacheTimeNumEnum timeNum, CacheTimeUnitEnum timeUnit) {
        throw new RuntimeException("not support!");
    }

    @Override
    @Deprecated
    public final void setCache(String invoker, String djxh, String cacheData, CacheTimeNumEnum timeNum, CacheTimeUnitEnum timeUnit) {
        throw new RuntimeException("not support!");
    }

    @Override
    @Deprecated
    public final boolean clearCache(String invoker, String djxh, String skssqq, String skssqz, int currentPage, int pageSize) {
        throw new RuntimeException("not support!");
    }

    @Override
    @Deprecated
    public final boolean clearCache(String invoker, String djxh, String skssqq, String skssqz) {
        throw new RuntimeException("not support!");
    }

    @Override
    @Deprecated
    public final boolean clearCache(String invoker, String djxh) {
        throw new RuntimeException("not support!");
    }

    @Override
    @Deprecated
    public final boolean deleteCache(String invoker, String djxh, String skssqq, String skssqz, int currentPage, int pageSize) {
        throw new RuntimeException("not support!");
    }

    @Override
    @Deprecated
    public final boolean deleteCache(String invoker, String djxh, String skssqq, String skssqz) {
        throw new RuntimeException("not support!");
    }

    @Override
    @Deprecated
    public final boolean deleteCache(String invoker, String djxh) {
        throw new RuntimeException("not support!");
    }

    /**
     * 加锁（获得锁文件）。
     *
     * @param lockFile      锁文件对象。
     * @param retryInterval 如果首次获得失败进行重试，每次重试之间的间隔(单位:ms)。
     * @param retryTimes    重试次数（如果设置为0则表示就获取一次，不做重试）。
     * @return
     */
    private boolean tryLock(final File lockFile, final int retryInterval, final int retryTimes) {
        final String LOG_PREFIX = "tryLock[uuid=" + uuid + "] ";
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "start! lockFile = " + lockFile + ", retryInterval = " + retryInterval + ", retryTimes = " + retryTimes);
        if (lockFile == null) {
            boolean result = false;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! lockFile is null, try lock fail! result = " + result);
            return result;
        }
        if (retryInterval < 0) {
            boolean result = false;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! retryInterval must more or eqaul 0, try lock fail! retryInterval = " + retryInterval + ", result = " + result);
            return result;
        }
        if (retryTimes < 0) {
            boolean result = false;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! retryTimes must more or eqaul 0, try lock fail! retryTimes = " + retryTimes + ", result = " + result);
            return result;
        }
        // 开始进行获取锁文件。
        int currentTimes = 0;
        do {
            // 第一次执行是正常获取的操作，不是重试行为；之后的循环才是重试。
            String retryLog = "";
            if (currentTimes > 0) {
                retryLog = "retry " + currentTimes + " time(s) ";
                // 休眠一段时间。
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + retryLog + "sleep current thread start!");
                try {
                    Thread.sleep(retryInterval);
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + retryLog + "sleep current thread end!");
                } catch (Exception e) {
                    logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + retryLog + "sleep current thread interrupted!", e);
                }
            }
            try {
                // 判断锁文件是否存在。
                if (!lockFile.exists()) {
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + retryLog + "lock file not exists!");
                    // 如果锁文件不存在，那么创建，并且如果创建成功，则表示锁定成功。
                    if (lockFile.createNewFile()) {
                        boolean result = true;
                        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + retryLog + "lock file create success, try lock success! result = " + result);
                        return result;
                    } else {
                        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + retryLog + "lock file create fail, try lock fail this time!");
                    }
                } else {
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + retryLog + "lock file exists, try lock fail this time!");
                }
            } catch (Exception e) {
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + retryLog + "lock file process error, try lock fail this time!", e);
            }
            currentTimes++;
        } while (currentTimes <= retryTimes);
        // 循环处理完成后依然没有获得锁文件，则返回失败。
        boolean result = false;
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! try lock fail! result = " + result);
        return result;
    }

    /**
     * 解锁（删除锁文件）。
     *
     * @param lockFile      锁文件对象。
     * @param retryInterval 如果首次删除失败进行重试，每次重试之间的间隔(单位:ms)。
     * @param retryTimes    重试次数（如果设置为0则表示就删除一次，不做重试）。
     * @return
     */
    private void unlock(final File lockFile, final int retryInterval, final int retryTimes) {
        final String LOG_PREFIX = "unlock[uuid=" + uuid + "] ";
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "start! lockFile = " + lockFile + ", retryInterval = " + retryInterval + ", retryTimes = " + retryTimes);
        if (lockFile == null) {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! lockFile is null, unlock fail!");
            return;
        }
        if (retryInterval < 0) {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! retryInterval must more or eqaul 0, unlock fail! retryInterval = " + retryInterval);
            return;
        }
        if (retryTimes < 0) {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! retryTimes must more or eqaul 0, unlock fail! retryTimes = " + retryTimes);
            return;
        }
        // 开始进行解锁。
        int currentTimes = 0;
        do {
            // 第一次执行是正常获取的操作，不是重试行为；之后的循环才是重试。
            String retryLog = "";
            if (currentTimes > 0) {
                retryLog = "retry " + currentTimes + " time(s) ";
                // 休眠一段时间。
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + retryLog + "sleep current thread start!");
                try {
                    Thread.sleep(retryInterval);
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + retryLog + "sleep current thread end!");
                } catch (Exception e) {
                    logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + retryLog + "sleep current thread interrupted!", e);
                }
            }
            try {
                // 判断锁文件是否存在。
                if (lockFile.exists()) {
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + retryLog + "lock file exists, delete it start!");
                    // 如果锁文件存在，那么删除，并且如果删除成功，则表示解锁成功。
                    if (lockFile.delete()) {
                        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + retryLog + "delete lock file success, unlock success!");
                        return;
                    } else {
                        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + retryLog + "delete lock file fail, unlock fail this time!");
                    }
                } else {
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + retryLog + "lock file not exists, unlock success but warn!");
                    return;
                }
            } catch (Exception e) {
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + retryLog + "lock file process error, unlock fail this time!", e);
            }
            currentTimes++;
        } while (currentTimes <= retryTimes);
        // 循环处理完成后依然没有删除锁文件，则表示解锁失败。
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! unlock fail!");
    }

    @Override
    public final CacheGetResult getCache(ICacheKeyCombine cacheKeyCombine, ICacheBalanceValue cacheBalanceValue) {
        long t = System.nanoTime();
        final String LOG_PREFIX = "getCache[uuid=" + uuid + "] ";
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "start! cacheKeyCombine = " + cacheKeyCombine + ", cacheBalanceValue = " + cacheBalanceValue);
        CacheGetResult result = null;
        if (!propertiesReaded) {
            result = CacheGetResult.DONT_HAVE;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! call readProperties first! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
        if (cacheKeyCombine == null) {
            result = CacheGetResult.DONT_HAVE;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cacheKeyCombine is null! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
        if (cacheBalanceValue == null) {
            result = CacheGetResult.DONT_HAVE;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cacheBalanceValue is null! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
        String cacheKey = cacheKeyCombine.getKey();
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache key = " + cacheKey);
        int balanceVaule = cacheBalanceValue.getBalanceVaule();
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "balance vaule = " + balanceVaule);
        // 根据均衡值和策略确定缓存文件。
        String balanceRepository = cacheBalanceStrategy.getBalanceRepository(balanceVaule);
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "balance repository = " + balanceRepository);
        if (balanceRepository == null) {
            result = CacheGetResult.DONT_HAVE;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! balance repository is null! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
        File cacheFile = new File(balanceRepository + File.separator + getCacheFileName(cacheKey));
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache file = " + cacheFile);
        // 开始读取缓存文件（读取不进行并发控制，而是进行数据自校验）。
        byte[] cacheFileBytes = new byte[0]; // 保存缓存文件内容。
        RandomAccessFile randomAccessFile = null;
        boolean randomAccessFileClosed = false;
        String timeoutInfo = "";
        try {
            // 判断缓存文件是否存在。
            if (!cacheFile.exists()) {
                result = CacheGetResult.DONT_HAVE;
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache file not exist! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                return result;
            }
            // 缓存文件存在，获取过期超时信息，超时信息包含在文件的头部几个字符中。
            randomAccessFile = new RandomAccessFile(cacheFile, "r");
            byte[] timeNumBytes = new byte[CacheTimeNumEnum.SIZE];
            int timeNumBytesReadResult = randomAccessFile.read(timeNumBytes);
            if (timeNumBytesReadResult != CacheTimeNumEnum.SIZE) {
                result = CacheGetResult.DONT_HAVE;
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! random access file read time num error! timeNumBytesReadResult and CacheTimeNumEnum.SIZE not equal! timeNumBytesReadResult = " + timeNumBytesReadResult + ", CacheTimeNumEnum.SIZE = " + CacheTimeNumEnum.SIZE + ", cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                return result;
            }
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "timeNumBytes = " + ArrayUtils.toString(timeNumBytes));
            String timeNum = new String(timeNumBytes, UTF_8);
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "timeNum = " + timeNum);
            CacheTimeNumEnum timeNumEnum = CacheTimeNumEnum.getEnum(timeNum);
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "timeNumEnum = " + timeNumEnum);
            if (timeNumEnum == null) {
                result = CacheGetResult.DONT_HAVE;
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache file error! timeNumEnum is null! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                return result;
            }
            byte[] timeUnitBytes = new byte[CacheTimeUnitEnum.SIZE];
            int timeUnitBytesReadResult = randomAccessFile.read(timeUnitBytes);
            if (timeUnitBytesReadResult != CacheTimeUnitEnum.SIZE) {
                result = CacheGetResult.DONT_HAVE;
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! random access file read time unit error! timeUnitBytesReadResult and CacheTimeUnitEnum.SIZE not equal! timeUnitBytesReadResult = " + timeUnitBytesReadResult + ", CacheTimeUnitEnum.SIZE = " + CacheTimeUnitEnum.SIZE + ", cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                return result;
            }
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "timeUnitBytes = " + ArrayUtils.toString(timeUnitBytes));
            String timeUnit = new String(timeUnitBytes, UTF_8);
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "timeUnit = " + timeUnit);
            CacheTimeUnitEnum timeUnitEnum = CacheTimeUnitEnum.getEnum(timeUnit);
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "timeUnitEnum = " + timeUnitEnum);
            if (timeUnitEnum == null) {
                result = CacheGetResult.DONT_HAVE;
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache file error! timeUnitEnum is null! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                return result;
            }
            timeoutInfo = timeNum + timeUnit;
            // 根据超时信息计算超时时长。
            long timeout = CacheTimeUnitEnum.getTimeout(timeNumEnum, timeUnitEnum);
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "timeout = " + timeout);
            if (timeout <= 0) {
                result = CacheGetResult.DONT_HAVE;
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! timeout error! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                return result;
            }
            // 判断文件是否过期。
            long currentTimeMillis = System.currentTimeMillis();
            long lastModified = cacheFile.lastModified();
            long period = currentTimeMillis - lastModified;
            if (period >= timeout) {
                // 注意：此处不主动删除文件。
                result = CacheGetResult.DONT_HAVE;
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache file timeout! currentTimeMillis = " + currentTimeMillis + ", lastModified = " + lastModified + ", period = " + period + ", timeout = " + timeout + ", cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                return result;
            }
            // 缓存文件存在并且没有过期，读取缓存文件。
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "get from cache file start!");
            // 重置文件指针至文件头。
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache file pointer reset to 0 start!");
            randomAccessFile.seek(0);
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache file pointer reset to 0 end!");
            // 开始读取。
            long randomAccessFileLength = randomAccessFile.length();
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "randomAccessFileLength = " + randomAccessFileLength);
            cacheFileBytes = new byte[Long.valueOf(randomAccessFileLength).intValue()];
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cacheFileBytes.length = " + cacheFileBytes.length);
            int randomAccessFileReadResult = randomAccessFile.read(cacheFileBytes);
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "randomAccessFileReadResult = " + randomAccessFileReadResult);
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "get from cache file end!");
            if (Integer.valueOf(randomAccessFileReadResult).longValue() != randomAccessFileLength) {
                result = CacheGetResult.DONT_HAVE;
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! random access file read error! randomAccessFileLength and randomAccessFileReadResult not equal! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                return result;
            }
        } catch (Exception e) {
            result = CacheGetResult.DONT_HAVE;
            logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "exception when read!", e);
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! read exception! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        } finally {
            // 关闭文件对象。
            try {
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "randomAccessFile close start!");
                if (randomAccessFile != null && !randomAccessFileClosed) {
                    randomAccessFile.close();
                }
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "randomAccessFile close end!");
            } catch (IOException e) {
                logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "randomAccessFile close fail, warn but continue!", e);
            }
        }
        // 判断保存缓存文件内容的字节数组。
        if (cacheFileBytes.length <= 0) {
            result = CacheGetResult.DONT_HAVE;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cacheFileBytes is empty! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
        // 返回结果。
        String cache = null;
        try {
            cache = new String(cacheFileBytes, UTF_8);
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache.length = " + cache.length());
            logger.debug(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache = " + cache);
            // 判断是否为NULL。
            if (cache.equals(timeoutInfo)) {
                result = CacheGetResult.NULL;
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache = " + cache + ", timeoutInfo = " + timeoutInfo + ", cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                return result;
            }
            // 判断是否为空串。
            if (cache.equals(timeoutInfo + CACHE_CONTENT_SEP)) {
                result = CacheGetResult.EMPTY;
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache = " + cache + ", timeoutInfo = " + timeoutInfo + ", CACHE_CONTENT_SEP = " + CACHE_CONTENT_SEP + ", cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                return result;
            }
            // 按照有数据的正常情况处理。
            String[] cacheContents = cache.split(CACHE_CONTENT_SEP);
            if (cacheContents == null) {
                result = CacheGetResult.DONT_HAVE;
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache error, because cache split by \"" + CACHE_CONTENT_SEP + "\" is null! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result + ", cache = " + cache);
                return result;
            }
            if (cacheContents.length < 3) { // 过期信息部分 + MD5部分 + 数据部分。考虑到数据中可能包含CACHE_CONTENT_SEP内容，所以此处不用!=比较。
                result = CacheGetResult.DONT_HAVE;
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache error, because cache split by \"" + CACHE_CONTENT_SEP + "\" length must more or equal 3! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result + ", length = " + cacheContents.length + ", cache = " + cache);
                return result;
            }
            // 获取MD5值(取下标为1的项，下标为0的项是过期信息)。
            String cacheMd5 = cacheContents[1];
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "md5 from cache file = " + cacheMd5);
            if (cacheMd5 == null) {
                result = CacheGetResult.DONT_HAVE;
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! md5 from cache file is null! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result + ", cache = " + cache);
                return result;
            }
            if (cacheMd5.isEmpty()) {
                result = CacheGetResult.DONT_HAVE;
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! md5 from cache file is empty! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result + ", cache = " + cache);
                return result;
            }
            // 获取缓存的数据。
            String cacheData = cache.substring(cache.indexOf(cacheMd5 + CACHE_CONTENT_SEP) + (cacheMd5 + CACHE_CONTENT_SEP).length());
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cacheData.length = " + (cacheData == null ? -1 : cacheData.length()));
            logger.debug(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cacheData = " + cacheData);
            if (cacheData == null) {
                result = CacheGetResult.DONT_HAVE;
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! data from cache file is null! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result + ", cache = " + cache);
                return result;
            }
            if (cacheData.isEmpty()) {
                result = CacheGetResult.DONT_HAVE;
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! data from cache file is empty! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result + ", cache = " + cache);
                return result;
            }
            // 验证MD5值。
            String cacheMd5ForCheck = CacheMd5.md5(cacheData, logger, LOG_PREFIX);
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "md5 for check = " + cacheMd5ForCheck);
            if (!cacheMd5.equals(cacheMd5ForCheck)) {
                result = CacheGetResult.DONT_HAVE;
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! md5 check not equal! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result + ", cacheData = " + cacheData + ", cache = " + cache);
                return result;
            }
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "md5 check ok!");
            result = new CacheGetResult(cacheData);
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cost(ns) = " + (System.nanoTime() - t) + ", result.isHave() = " + result.isHave() + ", result.getData().length() = " + result.getData().length());
            logger.debug(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! result = " + result);
            return result;
        } catch (Exception e) {
            result = CacheGetResult.DONT_HAVE;
            logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cacheFileBytes process error!", e);
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cacheFileBytes process error! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result + ", cache = " + cache);
            return result;
        }
    }

    @Override
    public void setCache(ICacheKeyCombine cacheKeyCombine, ICacheBalanceValue cacheBalanceValue, String cacheData, CacheTimeNumEnum timeNum, CacheTimeUnitEnum timeUnit) {
        long t = System.nanoTime();
        final String LOG_PREFIX = "setCache[uuid=" + uuid + "] ";
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "start! cacheKeyCombine = " + cacheKeyCombine + ", cacheBalanceValue = " + cacheBalanceValue + ", cacheData.length = " + (cacheData == null ? -1 : cacheData.length()) + ", timeNum = " + timeNum + ", timeUnit = " + timeUnit);
        if (!propertiesReaded) {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! call readProperties first! cost(ns) = " + (System.nanoTime() - t));
            return;
        }
        if (cacheKeyCombine == null) {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cacheKeyCombine is null! cost(ns) = " + (System.nanoTime() - t));
            return;
        }
        if (cacheBalanceValue == null) {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cacheBalanceValue is null! cost(ns) = " + (System.nanoTime() - t));
            return;
        }
        if (timeNum == null) {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! timeNum is null! cost(ns) = " + (System.nanoTime() - t));
            return;
        }
        if (timeUnit == null) {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! timeUnit is null! cost(ns) = " + (System.nanoTime() - t));
            return;
        }
        String cacheKey = cacheKeyCombine.getKey();
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache key = " + cacheKey);
        int balanceVaule = cacheBalanceValue.getBalanceVaule();
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "balance vaule = " + balanceVaule);
        // 根据均衡值和策略确定缓存文件及其锁文件。
        String balanceRepository = cacheBalanceStrategy.getBalanceRepository(balanceVaule);
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "balance repository = " + balanceRepository);
        if (balanceRepository == null) {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! balance repository is null! cost(ns) = " + (System.nanoTime() - t));
            return;
        }
        File cacheFile = new File(balanceRepository + File.separator + getCacheFileName(cacheKey));
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache file = " + cacheFile);
        File lockFile = new File(getLockDir(balanceRepository) + File.separator + getLockFileName(cacheKey));
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "lock file = " + lockFile);
        // 开始写入缓存文件（对缓存文件的操作要进行并发控制）。
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "tryLock start! nanoTime = " + System.nanoTime());
        // 加锁，否则等待超时。
        if (tryLock(lockFile, cacheLockInterval, cacheLockRetry)) {
            // 如果加锁成功。
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "tryLock success! nanoTime = " + System.nanoTime());
            long lockT = System.nanoTime();
            RandomAccessFile randomAccessFile = null;
            boolean randomAccessFileClosed = false;
            try {
                // 如果缓存文件存在。
                if (cacheFile.exists()) {
                    // 删除缓存文件。
                    boolean deleteResult = cacheFile.delete();
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache file exist, delete it! deleteResult = " + deleteResult);
                    if (!deleteResult) {
                        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache file exist but delete it fail! cost(ns) = " + (System.nanoTime() - t));
                        return;
                    }
                }
                // 创建缓存文件。
                boolean createNewFileResult = cacheFile.createNewFile();
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache file not exist and create new file! createNewFileResult = " + createNewFileResult);
                // 如果创建缓存文件失败。
                if (!createNewFileResult) {
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache file not exist but create new file fail! cost(ns) = " + (System.nanoTime() - t));
                    return;
                }
                // 如果创建缓存文件成功，构造缓存内容。
                StringBuffer cacheBuffer = new StringBuffer("");
                // PART1[timeout info]
                cacheBuffer.append(timeNum.getNum());
                cacheBuffer.append(timeUnit.getUnit());
                if (cacheData == null) {
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache data is null, create new file and only write PART1[timeout info]!");
                } else if (cacheData.isEmpty()) {
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache data is empty, create new file and only write PART1[timeout info], PART2[SEP(" + CACHE_CONTENT_SEP + ")]!");
                    // PART2[SEP]
                    cacheBuffer.append(CACHE_CONTENT_SEP);
                } else {
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache data is not null and is not empty, create new file and write PART1[timeout info], PART2[SEP(" + CACHE_CONTENT_SEP + ")], PART3[md5 for check], PART4[SEP(" + CACHE_CONTENT_SEP + ")], PART5[cache data]!");
                    // PART2[SEP]
                    cacheBuffer.append(CACHE_CONTENT_SEP);
                    // PART3[md5 for check]
                    String cacheMd5ForCheck = CacheMd5.md5(cacheData, logger, LOG_PREFIX);
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "md5 for check = " + cacheMd5ForCheck);
                    if (cacheMd5ForCheck == null) {
                        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! md5 for check is null! cost(ns) = " + (System.nanoTime() - t) + ", cacheData = " + cacheData);
                        return;
                    }
                    if (cacheMd5ForCheck.isEmpty()) {
                        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! md5 for check is empty! cost(ns) = " + (System.nanoTime() - t) + ", cacheData = " + cacheData);
                        return;
                    }
                    cacheBuffer.append(cacheMd5ForCheck);
                    // PART4[SEP]
                    cacheBuffer.append(CACHE_CONTENT_SEP);
                    // PART5[cache data]
                    cacheBuffer.append(cacheData);
                }
                String cache = cacheBuffer.toString();
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache.length = " + cache.length());
                logger.debug(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache = " + cache);
                // 写入缓存文件。
                byte[] cacheFileBytes = cache.getBytes(UTF_8);
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cacheFileBytes.length = " + cacheFileBytes.length);
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "write to cache file start!");
                randomAccessFile = new RandomAccessFile(cacheFile, "rw");
                randomAccessFile.write(cacheFileBytes);
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "write to cache file end!");
            } catch (Exception e) {
                logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "exception when write!", e);
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! write exception! cost(ns) = " + (System.nanoTime() - t));
                return;
            } finally {
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cost(ns) in lock block = " + (System.nanoTime() - lockT));
                // 关闭文件对象。
                try {
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "randomAccessFile close start!");
                    if (randomAccessFile != null && !randomAccessFileClosed) {
                        randomAccessFile.close();
                    }
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "randomAccessFile close end!");
                } catch (IOException e) {
                    logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "randomAccessFile close fail, warn but continue!", e);
                }
                // 解锁。
                unlock(lockFile, cacheLockInterval, cacheLockRetry);
            }
        } else {
            // 如果加锁失败。
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "tryLock fail! nanoTime = " + System.nanoTime());
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! tryLock fail! cost(ns) = " + (System.nanoTime() - t));
            return;
        }
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cost(ns) = " + (System.nanoTime() - t));
    }

    @Override
    public boolean clearCache(ICacheKeyCombine cacheKeyCombine, ICacheBalanceValue cacheBalanceValue) {
        long t = System.nanoTime();
        final String LOG_PREFIX = "clearCache[uuid=" + uuid + "] ";
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "start! cacheKeyCombine = " + cacheKeyCombine + ", cacheBalanceValue = " + cacheBalanceValue);
        if (!propertiesReaded) {
            boolean result = false;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! call readProperties first! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
        if (cacheKeyCombine == null) {
            boolean result = false;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cacheKeyCombine is null! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
        if (cacheBalanceValue == null) {
            boolean result = false;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cacheBalanceValue is null! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
        String cacheKey = cacheKeyCombine.getKey();
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache key = " + cacheKey);
        int balanceVaule = cacheBalanceValue.getBalanceVaule();
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "balance vaule = " + balanceVaule);
        // 根据均衡值和策略确定缓存文件及其锁文件。
        String balanceRepository = cacheBalanceStrategy.getBalanceRepository(balanceVaule);
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "balance repository = " + balanceRepository);
        if (balanceRepository == null) {
            boolean result = false;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! balance repository is null! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
        File cacheFile = new File(balanceRepository + File.separator + getCacheFileName(cacheKey));
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache file = " + cacheFile);
        File lockFile = new File(getLockDir(balanceRepository) + File.separator + getLockFileName(cacheKey));
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "lock file = " + lockFile);
        // 开始写入缓存文件（对缓存文件的操作要进行并发控制）。
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "tryLock start! nanoTime = " + System.nanoTime());
        // 加锁，否则等待超时。
        if (tryLock(lockFile, cacheLockInterval, cacheLockRetry)) {
            // 如果加锁成功。
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "tryLock success! nanoTime = " + System.nanoTime());
            long lockT = System.nanoTime();
            RandomAccessFile randomAccessFile = null;
            boolean randomAccessFileClosed = false;
            try {
                // 如果缓存文件存在。
                if (cacheFile.exists()) {
                    // 缓存文件存在，获取过期超时信息，超时信息包含在文件的头部几个字符中。
                    randomAccessFile = new RandomAccessFile(cacheFile, "r");
                    byte[] timeNumBytes = new byte[CacheTimeNumEnum.SIZE];
                    int timeNumBytesReadResult = randomAccessFile.read(timeNumBytes);
                    if (timeNumBytesReadResult != CacheTimeNumEnum.SIZE) {
                        boolean result = false;
                        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! random access file read time num error! timeNumBytesReadResult and CacheTimeNumEnum.SIZE not equal! timeNumBytesReadResult = " + timeNumBytesReadResult + ", CacheTimeNumEnum.SIZE = " + CacheTimeNumEnum.SIZE + ", cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                        return result;
                    }
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "timeNumBytes = " + ArrayUtils.toString(timeNumBytes));
                    String timeNum = new String(timeNumBytes, UTF_8);
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "timeNum = " + timeNum);
                    CacheTimeNumEnum timeNumEnum = CacheTimeNumEnum.getEnum(timeNum);
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "timeNumEnum = " + timeNumEnum);
                    if (timeNumEnum == null) {
                        boolean result = false;
                        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache file error! timeNumEnum is null! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                        return result;
                    }
                    byte[] timeUnitBytes = new byte[CacheTimeUnitEnum.SIZE];
                    int timeUnitBytesReadResult = randomAccessFile.read(timeUnitBytes);
                    if (timeUnitBytesReadResult != CacheTimeUnitEnum.SIZE) {
                        boolean result = false;
                        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! random access file read time unit error! timeUnitBytesReadResult and CacheTimeUnitEnum.SIZE not equal! timeUnitBytesReadResult = " + timeUnitBytesReadResult + ", CacheTimeUnitEnum.SIZE = " + CacheTimeUnitEnum.SIZE + ", cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                        return result;
                    }
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "timeUnitBytes = " + ArrayUtils.toString(timeUnitBytes));
                    String timeUnit = new String(timeUnitBytes, UTF_8);
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "timeUnit = " + timeUnit);
                    CacheTimeUnitEnum timeUnitEnum = CacheTimeUnitEnum.getEnum(timeUnit);
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "timeUnitEnum = " + timeUnitEnum);
                    if (timeUnitEnum == null) {
                        boolean result = false;
                        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache file error! timeUnitEnum is null! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                        return result;
                    }
                    // 关闭文件对象。
                    randomAccessFile.close();
                    randomAccessFileClosed = true;
                    // 根据超时信息计算超时时长。
                    long timeout = CacheTimeUnitEnum.getTimeout(timeNumEnum, timeUnitEnum);
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "timeout = " + timeout);
                    if (timeout <= 0) {
                        boolean result = false;
                        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! timeout error! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                        return result;
                    }
                    // 判断文件是否过期。
                    long currentTimeMillis = System.currentTimeMillis();
                    long lastModified = cacheFile.lastModified();
                    long period = currentTimeMillis - lastModified;
                    if (period >= timeout) {
                        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache file exist and timeout! currentTimeMillis = " + currentTimeMillis + ", lastModified = " + lastModified + ", period = " + period + ", timeout = " + timeout);
                        // 删除缓存文件。
                        boolean deleteResult = cacheFile.delete();
                        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache file exist and timeout, delete it! deleteResult = " + deleteResult);
                        if (!deleteResult) {
                            boolean result = false;
                            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache file exist and timeout but delete it fail! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                            return result;
                        } else {
                            boolean result = true;
                            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache file exist and timeout and delete it success! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                            return result;
                        }
                    } else {
                        boolean result = true;
                        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache file exist and not timeout, skip it! currentTimeMillis = " + currentTimeMillis + ", lastModified = " + lastModified + ", period = " + period + ", timeout = " + timeout + ", cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                        return result;
                    }
                } else {
                    boolean result = true;
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache file not exist! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                    return result;
                }
            } catch (Exception e) {
                boolean result = false;
                logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "exception when clear!", e);
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! clear exception! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                return result;
            } finally {
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cost(ns) in lock block = " + (System.nanoTime() - lockT));
                // 关闭文件对象。
                try {
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "randomAccessFile close start!");
                    if (randomAccessFile != null && !randomAccessFileClosed) {
                        randomAccessFile.close();
                    }
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "randomAccessFile close end!");
                } catch (IOException e) {
                    logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "randomAccessFile close fail, warn but continue!", e);
                }
                // 解锁。
                unlock(lockFile, cacheLockInterval, cacheLockRetry);
            }
        } else {
            // 如果加锁失败。
            boolean result = false;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "tryLock fail! nanoTime = " + System.nanoTime());
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! tryLock fail! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
    }

    @Override
    public boolean deleteCache(ICacheKeyCombine cacheKeyCombine, ICacheBalanceValue cacheBalanceValue) {
        long t = System.nanoTime();
        final String LOG_PREFIX = "deleteCache[uuid=" + uuid + "] ";
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "start! cacheKeyCombine = " + cacheKeyCombine + ", cacheBalanceValue = " + cacheBalanceValue);
        if (!propertiesReaded) {
            boolean result = false;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! call readProperties first! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
        if (cacheKeyCombine == null) {
            boolean result = false;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cacheKeyCombine is null! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
        if (cacheBalanceValue == null) {
            boolean result = false;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cacheBalanceValue is null! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
        String cacheKey = cacheKeyCombine.getKey();
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache key = " + cacheKey);
        int balanceVaule = cacheBalanceValue.getBalanceVaule();
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "balance vaule = " + balanceVaule);
        // 根据均衡值和策略确定缓存文件及其锁文件。
        String balanceRepository = cacheBalanceStrategy.getBalanceRepository(balanceVaule);
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "balance repository = " + balanceRepository);
        if (balanceRepository == null) {
            boolean result = false;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! balance repository is null! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
        File cacheFile = new File(balanceRepository + File.separator + getCacheFileName(cacheKey));
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache file = " + cacheFile);
        File lockFile = new File(getLockDir(balanceRepository) + File.separator + getLockFileName(cacheKey));
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "lock file = " + lockFile);
        // 开始写入缓存文件（对缓存文件的操作要进行并发控制）。
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "tryLock start! nanoTime = " + System.nanoTime());
        // 加锁，否则等待超时。
        if (tryLock(lockFile, cacheLockInterval, cacheLockRetry)) {
            // 如果加锁成功。
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "tryLock success! nanoTime = " + System.nanoTime());
            long lockT = System.nanoTime();
            RandomAccessFile randomAccessFile = null;
            boolean randomAccessFileClosed = false;
            try {
                // 如果缓存文件存在。
                if (cacheFile.exists()) {
                    // 删除缓存文件。
                    boolean deleteResult = cacheFile.delete();
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cache file exist, delete it! deleteResult = " + deleteResult);
                    if (!deleteResult) {
                        boolean result = false;
                        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache file exist but delete it fail! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                        return result;
                    } else {
                        boolean result = true;
                        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache file exist and delete it success! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                        return result;
                    }
                } else {
                    boolean result = true;
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache file not exist! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                    return result;
                }
            } catch (Exception e) {
                boolean result = false;
                logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "exception when delete!", e);
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! delete exception! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                return result;
            } finally {
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cost(ns) in lock block = " + (System.nanoTime() - lockT));
                // 关闭文件对象。
                try {
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "randomAccessFile close start!");
                    if (randomAccessFile != null && !randomAccessFileClosed) {
                        randomAccessFile.close();
                    }
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "randomAccessFile close end!");
                } catch (IOException e) {
                    logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "randomAccessFile close fail, warn but continue!", e);
                }
                // 解锁。
                unlock(lockFile, cacheLockInterval, cacheLockRetry);
            }
        } else {
            // 如果加锁失败。
            boolean result = false;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "tryLock fail! nanoTime = " + System.nanoTime());
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! tryLock fail! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
    }

    public static void main(String[] args) {
        final String[] nsrsbhList = new String[100];
        for (int i = 0; i < nsrsbhList.length; i++) {
            nsrsbhList[i] = String.valueOf(10000 + i);
        }
        final String[] dataList = new String[nsrsbhList.length];
        for (int i = 0; i < dataList.length; i++) {
            String data = "";
            if (i % 11 == 0) {
                data = null;
            } else if (i % 10 == 0) {
                data = "";
            } else {
                data = "ABCD_" + i;
            }
            dataList[i] = data;
        }
        List<Thread> setThreadList0 = new ArrayList<>();
        List<Thread> setThreadList1 = new ArrayList<>();
        List<Thread> getThreadList = new ArrayList<>();
        for (int i = 0; i < nsrsbhList.length; i++) {
            int index = i;
            Thread thread = new Thread(() -> {
                logger.info("Set0-" + index + " start!");
                SharedFileCache.getInstance().setCache(
                        () -> nsrsbhList[index],
                        () -> Integer.parseInt(nsrsbhList[index].substring(nsrsbhList[index].length() - 2)),
                        dataList[index],
                        CacheTimeNumEnum.N1,
                        CacheTimeUnitEnum.SECOND);
                logger.info("Set0-" + index + " end!");
            });
            thread.setName("S0_" + index);
            setThreadList0.add(thread);
        }
        for (int i = 0; i < nsrsbhList.length; i++) {
            int index = i;
            Thread thread = new Thread(() -> {
                logger.info("Set1-" + index + " start!");
                SharedFileCache.getInstance().setCache(
                        () -> nsrsbhList[index],
                        () -> Integer.parseInt(nsrsbhList[index].substring(nsrsbhList[index].length() - 2)),
                        dataList[index] + "!!!",
                        CacheTimeNumEnum.N2,
                        CacheTimeUnitEnum.SECOND);
                logger.info("Set1-" + index + " end!");
            });
            thread.setName("S1_" + index);
            setThreadList1.add(thread);
        }
        for (int i = 0; i < nsrsbhList.length; i++) {
            int index = i;
            Thread thread = new Thread(() -> {
                logger.info("Get-" + index + " start!");
                CacheGetResult result = SharedFileCache.getInstance().getCache(
                        () -> nsrsbhList[index],
                        () -> Integer.parseInt(nsrsbhList[index].substring(nsrsbhList[index].length() - 2)));
                logger.info("Get-" + index + " end : result = " + result);
                if (!result.isHave()) {
                    logger.error("Get-" + index + " dont have!");
                } else {
                    String data = result.getData();
                    if (index % 11 == 0) {
                        if (data == null) {
                            logger.info("Get-" + index + " OK!");
                        } else {
                            logger.error("Get-" + index + " not OK!");
                        }
                    } else if (index % 10 == 0) {
                        if ("".equals(data)) {
                            logger.info("Get-" + index + " OK!");
                        } else {
                            logger.error("Get-" + index + " not OK!");
                        }
                    } else {
                        if (("ABCD_" + index).equals(data)) {
                            logger.info("Get-" + index + " OK!");
                        } else {
                            logger.error("Get-" + index + " not OK!");
                        }
                    }
                }
            });
            thread.setName("G_" + index);
            getThreadList.add(thread);
        }
        for (int i = 0; i < nsrsbhList.length; i++) {
            setThreadList0.get(i).start();
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
            }
            setThreadList1.get(i).start();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
            getThreadList.get(i).start();
        }
    }
}
