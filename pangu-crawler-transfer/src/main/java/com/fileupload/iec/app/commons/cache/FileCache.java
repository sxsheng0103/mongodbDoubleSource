package com.fileupload.iec.app.commons.cache;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileCache extends AbstractCache implements ICache {

    private static final Logger logger = Logger.getLogger(FileCache.class);

    /**
     * Properties文件中缓存读锁超时时长(单位:毫秒)的Key。
     */
    private static final String CACHE_READ_LOCK_TIMEOUT_KEY = "file_cache_r_lock_timeout";

    /**
     * 默认的缓存读锁超时时长(单位:毫秒)。
     */
    private static final int DEFAULT_CACHE_READ_LOCK_TIMEOUT = 1000;

    /**
     * Properties文件中缓存写锁超时时长(单位:毫秒)的Key。
     */
    private static final String CACHE_WRITE_LOCK_TIMEOUT_KEY = "file_cache_w_lock_timeout";

    /**
     * 默认的缓存写锁超时时长(单位:毫秒)。
     */
    private static final int DEFAULT_CACHE_WRITE_LOCK_TIMEOUT = 1500;

    /**
     * Properties文件中缓存清除时写锁超时时长(单位:毫秒)的Key。
     */
    private static final String CACHE_CLEAN_LOCK_TIMEOUT_KEY = "file_cache_c_lock_timeout";

    /**
     * 默认的缓存清除时写锁超时时长(单位:毫秒)。
     */
    private static final int DEFAULT_CACHE_CLEAN_LOCK_TIMEOUT = 3000;

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
     * 默认安装登记序号的后两位进行哈希计算对应的缓存目录。
     */
    private static final int DJXH_LEAST_LEN = 2;

    /**
     * 缓存内容使用的分隔符。
     */
    private static final String CACHE_CONTENT_SEP = "#####";

    /**
     * 锁对象容器（KEY—缓存主键，即缓存文件名；VALUE—锁对象）。一个缓存文件一个锁对象。
     */
    private static final ConcurrentHashMap<String, ReentrantReadWriteLock> cacheLockMap = new ConcurrentHashMap<String, ReentrantReadWriteLock>();

    /**
     * 从Properties文件中解析完成标识。
     */
    private static volatile boolean propertiesReaded = false;

    /**
     * 从Properties文件中解析得到的缓存目录列表。
     */
    private static List<String> cacheDirList = null;

    /**
     * 从Properties文件中解析得到的读锁超时时长(单位:毫秒)。
     */
    private static int readLockTimeout = 0;

    /**
     * 从Properties文件中解析得到的写锁超时时长(单位:毫秒)。
     */
    private static int writeLockTimeout = 0;

    /**
     * 从Properties文件中解析得到的缓存清除时写锁超时时长(单位:毫秒)。
     */
    private static int cleanLockTimeout = 0;

    /**
     * 工作线程容器（KEY—缓存主键，对于文件缓存即缓存文件名；VALUE—对该缓存正在操作或等待操作的线程集合）。清除缓存时需要对这些线程进行打断等待的操作。
     */
    private static final ConcurrentHashMap<String, Set<Thread>> cacheThreadMap = new ConcurrentHashMap<String, Set<Thread>>();

    public static FileCache getInstance() {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        if (!readProperties(uuid, true)) {
            return null;
        }
        return new FileCache(uuid);
    }

    private String uuid;

    private FileCache(String uuid) {
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
            readLockTimeout = DEFAULT_CACHE_READ_LOCK_TIMEOUT;
            writeLockTimeout = DEFAULT_CACHE_WRITE_LOCK_TIMEOUT;
            cleanLockTimeout = DEFAULT_CACHE_CLEAN_LOCK_TIMEOUT;
            cacheDirProp = DEFAULT_CACHE_DIR;
        } else {
            // 获取读锁超时时长。
            // String readLockTimeoutProp = MinaLoadPrpo.getInstance().getValue(CACHE_READ_LOCK_TIMEOUT_KEY);
            String readLockTimeoutProp = "";
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "readLockTimeoutProp read from properties! key = " + CACHE_READ_LOCK_TIMEOUT_KEY + ", readLockTimeoutProp = " + readLockTimeoutProp);
            try {
                readLockTimeout = Integer.parseInt(readLockTimeoutProp);
            } catch (Exception e) {
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "readLockTimeoutProp read from properties parseInt fail, use default value! dafault is " + DEFAULT_CACHE_READ_LOCK_TIMEOUT);
                readLockTimeout = DEFAULT_CACHE_READ_LOCK_TIMEOUT;
            }
            // 获取写锁超时时长。
            // String writeLockTimeoutProp = MinaLoadPrpo.getInstance().getValue(CACHE_WRITE_LOCK_TIMEOUT_KEY);
            String writeLockTimeoutProp = "";
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLockTimeoutProp read from properties! key = " + CACHE_WRITE_LOCK_TIMEOUT_KEY + ", writeLockTimeoutProp = " + writeLockTimeoutProp);
            try {
                writeLockTimeout = Integer.parseInt(writeLockTimeoutProp);
            } catch (Exception e) {
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLockTimeoutProp read from properties parseInt fail, use default value! dafault is " + DEFAULT_CACHE_WRITE_LOCK_TIMEOUT);
                writeLockTimeout = DEFAULT_CACHE_WRITE_LOCK_TIMEOUT;
            }
            // 获取缓存清除时写锁超时时长。
            // String cleanLockTimeoutProp = MinaLoadPrpo.getInstance().getValue(CACHE_CLEAN_LOCK_TIMEOUT_KEY);
            String cleanLockTimeoutProp = "";
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cleanLockTimeoutProp read from properties! key = " + CACHE_CLEAN_LOCK_TIMEOUT_KEY + ", cleanLockTimeoutProp = " + cleanLockTimeoutProp);
            try {
                cleanLockTimeout = Integer.parseInt(cleanLockTimeoutProp);
            } catch (Exception e) {
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cleanLockTimeoutProp read from properties parseInt fail, use default value! dafault is " + DEFAULT_CACHE_CLEAN_LOCK_TIMEOUT);
                cleanLockTimeout = DEFAULT_CACHE_CLEAN_LOCK_TIMEOUT;
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
        // 处理缓存目录及父目录。
        if (!processCacheDir(cacheDirProp, LOG_PREFIX, t)) {
            return false;
        }
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cost(ns) = " + (System.nanoTime() - t));
        propertiesReaded = true;
        return true;
    }

    /**
     * 处理缓存目录及父目录。
     *
     * @param cacheDirProp
     * @param logPrefix
     * @param t
     * @return
     */
    private static synchronized boolean processCacheDir(String cacheDirProp, String logPrefix, long t) {
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

    private File getCacheFile(String invoker, String djxh, String skssqq, String skssqz, String currentPage, String pageSize, String logPrefix, long t) {
        if (!propertiesReaded) {
            logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "end! call readProperties first! cost(ns) = " + (System.nanoTime() - t));
            return null;
        }
        // 判断参数
        if (djxh == null) {
            logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "end! djxh is null! cost(ns) = " + (System.nanoTime() - t));
            return null;
        }
        if (djxh.isEmpty()) {
            logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "end! djxh is empty! cost(ns) = " + (System.nanoTime() - t));
            return null;
        }
        if (djxh.length() < DJXH_LEAST_LEN) {
            logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "end! djxh not enough length! cost(ns) = " + (System.nanoTime() - t) + ", djxh = " + djxh + ", DJXH_LEAST_LEN = " + DJXH_LEAST_LEN);
            return null;
        }
        if (skssqq == null) {
            logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "end! skssqq is null! cost(ns) = " + (System.nanoTime() - t));
            return null;
        }
        if (skssqq.isEmpty()) {
            logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "end! skssqq is empty! cost(ns) = " + (System.nanoTime() - t));
            return null;
        }
        if (skssqz == null) {
            logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "end! skssqz is null! cost(ns) = " + (System.nanoTime() - t));
            return null;
        }
        if (skssqz.isEmpty()) {
            logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "end! skssqz is empty! cost(ns) = " + (System.nanoTime() - t));
            return null;
        }
        if (currentPage == null) {
            currentPage = "";
        }
        if (pageSize == null) {
            pageSize = "";
        }
        // 根据登记序号确定缓存目录。
        int cacheDirIndex = 0;
        try {
            // 根据登记序号最后两位计算。
            String djxhLeast = djxh.substring(djxh.length() - DJXH_LEAST_LEN);
            cacheDirIndex = Integer.parseInt(djxhLeast) % cacheDirList.size();
        } catch (Exception e) {
            logger.error(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "cacheDirIndex calc fail, set cacheDirIndex = 0 ! ", e);
            cacheDirIndex = 0;
        }
        String cacheDir = cacheDirList.get(cacheDirIndex);
        logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "cacheDirIndex = " + cacheDirIndex + ", cacheDir = " + cacheDir);
        // 获取缓存主键。
        StringBuffer cacheKey = new StringBuffer(invoker);
        cacheKey.append(djxh);
        cacheKey.append(skssqq);
        cacheKey.append(skssqz);
        cacheKey.append(currentPage);
        cacheKey.append(pageSize);
        logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "cacheKey = " + cacheKey);
        // 缓存主键作为文件名。
        String cacheFileName = cacheKey.toString() + ".cache";
        File cacheFile = new File(cacheDir + File.separator + cacheFileName);
        logger.info(logPrefix + "<t(ns):" + System.nanoTime() + "> " + "cacheFile = " + cacheFile);
        return cacheFile;
    }

    @Override
    protected final CacheGetResult getCache(String invoker, String djxh, String skssqq, String skssqz, String currentPage, String pageSize) {
        long t = System.nanoTime();
        final String LOG_PREFIX = "getCache[uuid=" + uuid + "] ";
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "start! invoker = " + invoker + ", djxh = " + djxh + ", skssqq = " + skssqq + ", skssqz = " + skssqz + ", currentPage = " + currentPage + ", pageSize = " + pageSize);
        CacheGetResult result = null;
        if (!propertiesReaded) {
            result = CacheGetResult.DONT_HAVE;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! call readProperties first! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
        File cacheFile = getCacheFile(invoker, djxh, skssqq, skssqz, currentPage, pageSize, LOG_PREFIX, t);
        if (cacheFile == null) {
            result = CacheGetResult.DONT_HAVE;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache file is null! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
        // 开始读取缓存文件。
        byte[] cacheFileBytes = new byte[0]; // 保存缓存文件内容。
        // 首先按缓存文件生成锁对象。
        String cacheLockKey = cacheFile.getName();
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cacheLockKey = " + cacheLockKey);
        // 保存线程(执行完成后移除)。
        addCacheThread(cacheThreadMap, cacheLockKey, Thread.currentThread(), LOG_PREFIX);
        // 获取锁对象。
        cacheLockMap.putIfAbsent(cacheLockKey, new ReentrantReadWriteLock());
        ReentrantReadWriteLock lock = cacheLockMap.get(cacheLockKey);
        // 对缓存文件的操作要进行并发控制。
        String timeoutInfo = null;
        try {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "readLock tryLock start! nanoTime = " + System.nanoTime());
            // 加锁，否则等待超时。
            if (lock.readLock().tryLock() || lock.readLock().tryLock(readLockTimeout, TimeUnit.MILLISECONDS)) {
                // 如果加锁成功。
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "readLock tryLock success! nanoTime = " + System.nanoTime());
                long lockT = System.nanoTime();
                RandomAccessFile randomAccessFile = null;
                boolean randomAccessFileClosed = false;
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
                    logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "exception in lock block!", e);
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! lock block exception! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                    return result;
                } finally {
                    // 关闭文件对象。
                    try {
                        if (randomAccessFile != null && !randomAccessFileClosed) {
                            randomAccessFile.close();
                        }
                    } catch (IOException e) {
                        logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "randomAccessFile close fail, warn but continue!", e);
                    }
                    // 解锁。
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "readLock unlock! cost(ns) in lock block = " + (System.nanoTime() - lockT));
                    lock.readLock().unlock();
                    // 移除线程。
                    removeCacheThread(cacheThreadMap, cacheLockKey, Thread.currentThread(), LOG_PREFIX);
                }
            } else {
                // 如果加锁失败。
                result = CacheGetResult.DONT_HAVE;
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "readLock tryLock fail! nanoTime = " + System.nanoTime());
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! readLock tryLock fail! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                // 移除线程。
                removeCacheThread(cacheThreadMap, cacheLockKey, Thread.currentThread(), LOG_PREFIX);
                return result;
            }
        } catch (InterruptedException e) {
            // 加锁等待过程中被打断。
            result = CacheGetResult.DONT_HAVE;
            logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "readLock tryLock interrupted!", e);
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! readLock tryLock interrupted! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            // 移除线程。
            removeCacheThread(cacheThreadMap, cacheLockKey, Thread.currentThread(), LOG_PREFIX);
            return result;
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
    protected final void setCache(String invoker, String djxh, String skssqq, String skssqz, String currentPage, String pageSize, String cacheData, CacheTimeNumEnum timeNum, CacheTimeUnitEnum timeUnit) {
        long t = System.nanoTime();
        final String LOG_PREFIX = "setCache[uuid=" + uuid + "] ";
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "start! invoker = " + invoker + ", djxh = " + djxh + ", skssqq = " + skssqq + ", skssqz = " + skssqz + ", currentPage = " + currentPage + ", pageSize = " + pageSize + ", cacheData.length = " + (cacheData == null ? -1 : cacheData.length()) + ", timeNum = " + timeNum + ", timeUnit = " + timeUnit);
        logger.debug(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "start! cacheData = " + cacheData);
        if (!propertiesReaded) {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! call readProperties first! cost(ns) = " + (System.nanoTime() - t));
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
        File cacheFile = getCacheFile(invoker, djxh, skssqq, skssqz, currentPage, pageSize, LOG_PREFIX, t);
        if (cacheFile == null) {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache file is null! cost(ns) = " + (System.nanoTime() - t));
            return;
        }
        // 开始写入缓存文件。
        // 首先按缓存文件生成锁对象。
        final String cacheLockKey = cacheFile.getName();
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cacheLockKey = " + cacheLockKey);
        // 保存线程(执行完成后移除)。
        addCacheThread(cacheThreadMap, cacheLockKey, Thread.currentThread(), LOG_PREFIX);
        // 获取锁对象。
        cacheLockMap.putIfAbsent(cacheLockKey, new ReentrantReadWriteLock());
        ReentrantReadWriteLock lock = cacheLockMap.get(cacheLockKey);
        // 对缓存文件的操作要进行并发控制。
        try {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLock tryLock start! nanoTime = " + System.nanoTime());
            // 加锁，否则等待超时。
            if (lock.writeLock().tryLock() || lock.writeLock().tryLock(writeLockTimeout, TimeUnit.MILLISECONDS)) {
                // 如果加锁成功。
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLock tryLock success! nanoTime = " + System.nanoTime());
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
                    logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "exception in lock block!", e);
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! lock block exception! cost(ns) = " + (System.nanoTime() - t));
                    return;
                } finally {
                    // 关闭文件对象。
                    try {
                        if (randomAccessFile != null && !randomAccessFileClosed) {
                            randomAccessFile.close();
                        }
                    } catch (IOException e) {
                        logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "randomAccessFile close fail, warn but continue!", e);
                    }
                    // 解锁
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLock unlock! cost(ns) in lock block = " + (System.nanoTime() - lockT));
                    lock.writeLock().unlock();
                    // 移除线程。
                    removeCacheThread(cacheThreadMap, cacheLockKey, Thread.currentThread(), LOG_PREFIX);
                }
            } else {
                // 如果加锁失败。
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLock tryLock fail! nanoTime = " + System.nanoTime());
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! writeLock tryLock fail! cost(ns) = " + (System.nanoTime() - t));
                // 移除线程。
                removeCacheThread(cacheThreadMap, cacheLockKey, Thread.currentThread(), LOG_PREFIX);
                return;
            }
        } catch (InterruptedException e) {
            // 加锁等待过程中被打断。
            logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLock tryLock interrupted!", e);
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! writeLock tryLock interrupted! cost(ns) = " + (System.nanoTime() - t));
            // 移除线程。
            removeCacheThread(cacheThreadMap, cacheLockKey, Thread.currentThread(), LOG_PREFIX);
            return;
        }
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cost(ns) = " + (System.nanoTime() - t));
    }

    @Override
    protected final boolean clearCache(String invoker, String djxh, String skssqq, String skssqz, String currentPage, String pageSize) {
        long t = System.nanoTime();
        final String LOG_PREFIX = "clearCache[uuid=" + uuid + "] ";
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "start! invoker = " + invoker + ", djxh = " + djxh + ", skssqq = " + skssqq + ", skssqz = " + skssqz + ", currentPage = " + currentPage + ", pageSize = " + pageSize);
        if (!propertiesReaded) {
            boolean result = false;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! call readProperties first! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
        File cacheFile = getCacheFile(invoker, djxh, skssqq, skssqz, currentPage, pageSize, LOG_PREFIX, t);
        if (cacheFile == null) {
            boolean result = true;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache file is null! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
        // 开始清除缓存文件。
        // 首先按缓存文件生成锁对象。
        String cacheLockKey = cacheFile.getName();
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cacheLockKey = " + cacheLockKey);
        cacheLockMap.putIfAbsent(cacheLockKey, new ReentrantReadWriteLock());
        ReentrantReadWriteLock lock = cacheLockMap.get(cacheLockKey);
        // 然后打断其它等待锁的线程。
        Set<Thread> cacheThreads = removeAndGetCacheThreads(cacheThreadMap, cacheLockKey, LOG_PREFIX);
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "interrupt cacheThreads start! cacheThreads = " + cacheThreads);
        Iterator<Thread> iterator = cacheThreads.iterator();
        while (iterator.hasNext()) {
            Thread thread = iterator.next();
            if (thread == null) {
                continue;
            }
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "interrupt thread start! thread = " + thread);
            thread.interrupt();
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "interrupt thread end! thread = " + thread);
        }
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "interrupt cacheThreads end! cacheThreads = " + cacheThreads);
        // 对缓存文件的操作要进行并发控制。
        try {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLock tryLock start! nanoTime = " + System.nanoTime());
            // 加锁，否则等待超时。
            if (lock.writeLock().tryLock() || lock.writeLock().tryLock(cleanLockTimeout, TimeUnit.MILLISECONDS)) {
                // 如果加锁成功。
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLock tryLock success! nanoTime = " + System.nanoTime());
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
                    logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "exception in lock block!", e);
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! lock block exception! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                    return result;
                } finally {
                    // 关闭文件对象。
                    try {
                        if (randomAccessFile != null && !randomAccessFileClosed) {
                            randomAccessFile.close();
                        }
                    } catch (IOException e) {
                        logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "randomAccessFile close fail, warn but continue!", e);
                    }
                    // 解锁
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLock unlock! cost(ns) in lock block = " + (System.nanoTime() - lockT));
                    lock.writeLock().unlock();
                }
            } else {
                // 如果加锁失败。
                boolean result = false;
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLock tryLock fail! nanoTime = " + System.nanoTime());
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! writeLock tryLock fail! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                return result;
            }
        } catch (InterruptedException e) {
            // 加锁等待过程中被打断。
            boolean result = false;
            logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLock tryLock interrupted!", e);
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! writeLock tryLock interrupted! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
    }

    @Override
    protected final boolean deleteCache(String invoker, String djxh, String skssqq, String skssqz, String currentPage, String pageSize) {
        long t = System.nanoTime();
        final String LOG_PREFIX = "deleteCache[uuid=" + uuid + "] ";
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "start! invoker = " + invoker + ", djxh = " + djxh + ", skssqq = " + skssqq + ", skssqz = " + skssqz + ", currentPage = " + currentPage + ", pageSize = " + pageSize);
        if (!propertiesReaded) {
            boolean result = false;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! call readProperties first! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
        File cacheFile = getCacheFile(invoker, djxh, skssqq, skssqz, currentPage, pageSize, LOG_PREFIX, t);
        if (cacheFile == null) {
            boolean result = true;
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cache file is null! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
            return result;
        }
        // 开始删除缓存文件。
        // 首先按缓存文件生成锁对象。
        String cacheLockKey = cacheFile.getName();
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "cacheLockKey = " + cacheLockKey);
        cacheLockMap.putIfAbsent(cacheLockKey, new ReentrantReadWriteLock());
        ReentrantReadWriteLock lock = cacheLockMap.get(cacheLockKey);
        // 然后打断其它等待锁的线程。
        Set<Thread> cacheThreads = removeAndGetCacheThreads(cacheThreadMap, cacheLockKey, LOG_PREFIX);
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "interrupt cacheThreads start! cacheThreads = " + cacheThreads);
        Iterator<Thread> iterator = cacheThreads.iterator();
        while (iterator.hasNext()) {
            Thread thread = iterator.next();
            if (thread == null) {
                continue;
            }
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "interrupt thread start! thread = " + thread);
            thread.interrupt();
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "interrupt thread end! thread = " + thread);
        }
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "interrupt cacheThreads end! cacheThreads = " + cacheThreads);
        // 对缓存文件的操作要进行并发控制。
        try {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLock tryLock start! nanoTime = " + System.nanoTime());
            // 加锁，否则等待超时。
            if (lock.writeLock().tryLock() || lock.writeLock().tryLock(cleanLockTimeout, TimeUnit.MILLISECONDS)) {
                // 如果加锁成功。
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLock tryLock success! nanoTime = " + System.nanoTime());
                long lockT = System.nanoTime();
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
                    logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "exception in lock block!", e);
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! lock block exception! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                    return result;
                } finally {
                    // 解锁
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLock unlock! cost(ns) in lock block = " + (System.nanoTime() - lockT));
                    lock.writeLock().unlock();
                }
            } else {
                // 如果加锁失败。
                boolean result = false;
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLock tryLock fail! nanoTime = " + System.nanoTime());
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! writeLock tryLock fail! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
                return result;
            }
        } catch (InterruptedException e) {
            // 加锁等待过程中被打断。
            boolean result = false;
            logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLock tryLock interrupted!", e);
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! writeLock tryLock interrupted! cost(ns) = " + (System.nanoTime() - t) + ", result = " + result);
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
                data = "XYZ_" + i;
            }
            dataList[i] = data;
        }
        List<Thread> setThreadList0 = new ArrayList<>();
        List<Thread> setThreadList1 = new ArrayList<>();
        List<Thread> getThreadList = new ArrayList<>();
        for (int i = 0; i < nsrsbhList.length; i++) {
            int index = i;
            Thread thread = new Thread(() -> {
                logger.info("FSet0-" + index + " start!");
                FileCache.getInstance().setCache(
                        () -> nsrsbhList[index],
                        () -> Integer.parseInt(nsrsbhList[index].substring(nsrsbhList[index].length() - 2)),
                        dataList[index],
                        CacheTimeNumEnum.N1,
                        CacheTimeUnitEnum.SECOND);
                logger.info("FSet0-" + index + " end!");
            });
            thread.setName("FS0_" + index);
            setThreadList0.add(thread);
        }
        for (int i = 0; i < nsrsbhList.length; i++) {
            int index = i;
            Thread thread = new Thread(() -> {
                logger.info("FSet1-" + index + " start!");
                FileCache.getInstance().setCache(
                        () -> nsrsbhList[index],
                        () -> Integer.parseInt(nsrsbhList[index].substring(nsrsbhList[index].length() - 2)),
                        dataList[index] + "!!!",
                        CacheTimeNumEnum.N2,
                        CacheTimeUnitEnum.SECOND);
                logger.info("FSet1-" + index + " end!");
            });
            thread.setName("FS1_" + index);
            setThreadList1.add(thread);
        }
        for (int i = 0; i < nsrsbhList.length; i++) {
            int index = i;
            Thread thread = new Thread(() -> {
                logger.info("FGet-" + index + " start!");
                CacheGetResult result = FileCache.getInstance().getCache(
                        () -> nsrsbhList[index],
                        () -> Integer.parseInt(nsrsbhList[index].substring(nsrsbhList[index].length() - 2)));
                logger.info("FGet-" + index + " end : result = " + result);
                if (!result.isHave()) {
                    logger.error("FGet-" + index + " dont have!");
                } else {
                    String data = result.getData();
                    if (index % 11 == 0) {
                        if (data == null) {
                            logger.info("FGet-" + index + " OK!");
                        } else {
                            logger.error("FGet-" + index + " not OK!");
                        }
                    } else if (index % 10 == 0) {
                        if ("".equals(data)) {
                            logger.info("FGet-" + index + " OK!");
                        } else {
                            logger.error("FGet-" + index + " not OK!");
                        }
                    } else {
                        if (("XYZ_" + index).equals(data)) {
                            logger.info("FGet-" + index + " OK!");
                        } else {
                            logger.error("FGet-" + index + " not OK!");
                        }
                    }
                }
            });
            thread.setName("FG_" + index);
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
