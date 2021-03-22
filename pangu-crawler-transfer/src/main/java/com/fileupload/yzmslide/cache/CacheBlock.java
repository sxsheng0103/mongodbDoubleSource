package com.fileupload.yzmslide.cache;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.nio.charset.StandardCharsets.UTF_8;

class CacheBlock {

    private static final Logger logger = Logger.getLogger(CacheBlock.class);

    /**
     * 该缓存块在缓存块列表中的下标位置。
     */
    private final int indexOfCacheBlockList;

    /**
     * 该缓存块在缓存区中的起始下标位置。
     */
    private final int startIndexOfCacheArea;

    /**
     * 该缓存块大小（字节）。
     */
    private final int cacheBlockByteSize;

    /**
     * 对缓存区的引用。
     */
    private final byte[] cacheArea;

    /**
     * 对该缓存块读写锁的超时时长（单位：ms）。
     */
    private final int lockTimeout;

    /**
     * 该缓存块读写锁对象。
     */
    private final ReentrantReadWriteLock lock;

    /**
     * 日志前缀。
     */
    private final String BASE_LOG_PREFIX;

    public CacheBlock(int indexOfCacheBlockList, int startIndexOfCacheArea, int cacheBlockByteSize, byte[] cacheArea, int lockTimeout) {
        this.indexOfCacheBlockList = indexOfCacheBlockList;
        this.startIndexOfCacheArea = startIndexOfCacheArea;
        this.cacheBlockByteSize = cacheBlockByteSize;
        this.cacheArea = cacheArea;
        this.lockTimeout = lockTimeout;
        this.lock = new ReentrantReadWriteLock();
        StringBuffer sb = new StringBuffer("CacheBlock");
        sb.append("[indexOfCacheBlockList=").append(indexOfCacheBlockList).append("]");
        sb.append("[startIndexOfCacheArea=").append(startIndexOfCacheArea).append("]");
        sb.append("[cacheBlockByteSize=").append(cacheBlockByteSize).append("]");
        sb.append("[cacheArea.length=").append(cacheArea.length).append("]");
        sb.append("[lockTimeout=").append(lockTimeout).append("(ms)]");
        this.BASE_LOG_PREFIX = sb.toString();
    }

    public int getIndexOfCacheBlockList() {
        return indexOfCacheBlockList;
    }

    public int getStartIndexOfCacheArea() {
        return startIndexOfCacheArea;
    }

    public int getCacheBlockByteSize() {
        return cacheBlockByteSize;
    }

    public byte[] getCacheArea() {
        return cacheArea;
    }

    @Override
    public int hashCode() {
        return indexOfCacheBlockList;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CacheBlock other = (CacheBlock) obj;
        if (indexOfCacheBlockList != other.indexOfCacheBlockList) {
            return false;
        }
        return true;
    }

    /**
     * 获取该缓存块内容，返回副本，如果获取失败则返回NULL。
     */
    public byte[] getBlock(String uuid) {
        long t = System.nanoTime();
        final String LOG_PREFIX = BASE_LOG_PREFIX + "{getBlock}[uuid=" + uuid + "][Thread=" + Thread.currentThread().getName() + "] ";
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "start!");
        // 对缓存块的操作要进行并发控制。
        try {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "readLock tryLock start! nanoTime = " + System.nanoTime());
            // 加锁，否则等待超时。
            if (lock.readLock().tryLock() || lock.readLock().tryLock(lockTimeout, TimeUnit.MILLISECONDS)) {
                // 如果加锁成功。
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "readLock tryLock success! nanoTime = " + System.nanoTime());
                long lockT = System.nanoTime();
                try {
                    // 构造返回的副本结果。
                    byte[] block = new byte[cacheBlockByteSize];
                    System.arraycopy(cacheArea, startIndexOfCacheArea, block, 0, cacheBlockByteSize);
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cost(ns) = " + (System.nanoTime() - t));
                    return block;
                } catch (Exception e) {
                    logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "exception in lock block!", e);
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! lock block exception! cost(ns) = " + (System.nanoTime() - t) + ", result is null!");
                    return null;
                } finally {
                    // 解锁。
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "readLock unlock! cost(ns) in lock block = " + (System.nanoTime() - lockT));
                    lock.readLock().unlock();
                }
            } else {
                // 如果加锁失败。
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "readLock tryLock fail! nanoTime = " + System.nanoTime());
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! readLock tryLock fail! cost(ns) = " + (System.nanoTime() - t) + ", result is null!");
                return null;
            }
        } catch (InterruptedException e) {
            // 加锁等待过程中被打断。
            logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "readLock tryLock interrupted!", e);
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! readLock tryLock interrupted! cost(ns) = " + (System.nanoTime() - t) + ", result is null!");
            return null;
        }
    }

    /**
     * 设置该缓存块内容，按照多退少补(补0)的原则自动拷贝至缓存块中。
     *
     * @param uuid
     * @param block      准备缓存的内容数组(非空)。
     * @param startIndex 准备缓存的内容数组中被缓存内容的开始下标。
     * @return 是否设置成功
     */
    public boolean setBlock(String uuid, byte[] block, int startIndex) {
        long t = System.nanoTime();
        final String LOG_PREFIX = BASE_LOG_PREFIX + "{setBlock}[uuid=" + uuid + "][Thread=" + Thread.currentThread().getName() + "] ";
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "start! block.length = " + (block == null ? -1 : block.length) + ", startIndex = " + startIndex);
        // 判断参数。
        if (block == null) {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end, block is null! cost(ns) = " + (System.nanoTime() - t) + ", result is false!");
            return false;
        }
        if (block.length <= 0) {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end, block is empty! cost(ns) = " + (System.nanoTime() - t) + ", result is false!");
            return false;
        }
        if (startIndex < 0) {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end, startIndex error! cost(ns) = " + (System.nanoTime() - t) + ", result is false!");
            return false;
        }
        if (startIndex >= block.length) {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end, startIndex not less than block.length! cost(ns) = " + (System.nanoTime() - t) + ", result is false!");
            return false;
        }
        // 构造被缓存的数组并初始化。
        byte[] tobeSetBlock = new byte[cacheBlockByteSize];
        for (int i = 0; i < cacheBlockByteSize; i++) {
            tobeSetBlock[i] = 0;
        }
        // 计算被缓存的长度。
        int tobeSetBlockSize = Math.min(block.length - startIndex, cacheBlockByteSize);
        logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + " calc tobe set block size = " + tobeSetBlockSize + "! block.length = " + block.length + ", startIndex = " + startIndex + ", block.length - startIndex = " + (block.length - startIndex) + ", cacheBlockByteSize = " + cacheBlockByteSize);
        // 拷贝需要被缓存的数据（如果被缓存的长度小于该缓存块的大小，剩余部分就是初始化时的0）。
        System.arraycopy(block, startIndex, tobeSetBlock, 0, tobeSetBlockSize);
        // 对缓存块的操作要进行并发控制。
        try {
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLock tryLock start! nanoTime = " + System.nanoTime());
            // 加锁，否则等待超时。
            if (lock.writeLock().tryLock() || lock.writeLock().tryLock(lockTimeout, TimeUnit.MILLISECONDS)) {
                // 如果加锁成功。
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLock tryLock success! nanoTime = " + System.nanoTime());
                long lockT = System.nanoTime();
                try {
                    // 将需要缓存的数据拷贝至缓存区（必须将整块缓存块拷贝过去）。
                    System.arraycopy(tobeSetBlock, 0, cacheArea, startIndexOfCacheArea, cacheBlockByteSize);
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! cost(ns) = " + (System.nanoTime() - t));
                    return true;
                } catch (Exception e) {
                    logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "exception in lock block!", e);
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! lock block exception! cost(ns) = " + (System.nanoTime() - t) + ", result is false!");
                    return false;
                } finally {
                    // 解锁。
                    logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLock unlock! cost(ns) in lock block = " + (System.nanoTime() - lockT));
                    lock.writeLock().unlock();
                }
            } else {
                // 如果加锁失败。
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLock tryLock fail! nanoTime = " + System.nanoTime());
                logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! writeLock tryLock fail! cost(ns) = " + (System.nanoTime() - t) + ", result is false!");
                return false;
            }
        } catch (InterruptedException e) {
            // 加锁等待过程中被打断。
            logger.error(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "writeLock tryLock interrupted!", e);
            logger.info(LOG_PREFIX + "<t(ns):" + System.nanoTime() + "> " + "end! writeLock tryLock interrupted! cost(ns) = " + (System.nanoTime() - t) + ", result is false!");
            return false;
        }
    }

    public static void main(String[] args) {
        final int N = 14;
        final String[] uuids = new String[N];
        for (int i = 0; i < N; i++) {
            String uuid = UUID.randomUUID().toString();
            uuid = uuid.replace("-", "");
            uuids[i] = uuid;
        }
        int size = 0;
        for (int i = 0; i < N; i++) {
            int blockSize = N + i;
            size += blockSize;
        }
        final byte[] cacheArea = new byte[size];
        final CacheBlock[] cacheBlocks = new CacheBlock[N];
        int index = 0;
        for (int i = 0; i < N; i++) {
            int blockSize = N + i;
            cacheBlocks[i] = new CacheBlock(i, index, blockSize, cacheArea, 5);
            index += blockSize;
        }
        final String[] caches = new String[N];
        for (int i = 0; i < N; i++) {
            String cache = "";
            if (i % 9 == 0) {
                cache = " ";
            } else if (i % 11 == 0) {
                cache = "\n";
            } else if (i % 13 == 0) {
                cache = "\t";
            } else {
                for (int j = 0; j <= 3 * i; j++) {
                    cache += String.valueOf(j % 10);
                }
            }
            caches[i] = cache;
        }
        List<Thread> setThreadList = new ArrayList<Thread>();
        List<Thread> getThreadList = new ArrayList<Thread>();
        for (int i = 0; i < N; i++) {
            final int ci = i;
            final String cache = caches[i];
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    String uuid = uuids[ci];
                    CacheBlock cacheBlock = cacheBlocks[ci];
                    boolean setResult = false;
                    try {
                        byte[] cacheBytes = cache.getBytes(UTF_8);
                        byte[] cacheBytes2 = new byte[cacheBytes.length + 1];
                        System.arraycopy(cacheBytes, 0, cacheBytes2, 0, cacheBytes.length);
                        cacheBytes2[cacheBytes2.length - 1] = 1;
                        setResult = cacheBlock.setBlock(uuid, cacheBytes2, 0);
                    } catch (Exception e) {
                        setResult = false;
                        e.printStackTrace();
                    }
                    logger.error(uuid + " Set-Test-" + ci + " : setResult = " + setResult + ", cache.length() = " + (cache == null ? -1 : cache.length()));
                }
            });
            setThreadList.add(thread);
        }
        for (int i = 0; i < N; i++) {
            final int ci = i;
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    String uuid = uuids[ci];
                    CacheBlock cacheBlock = cacheBlocks[ci];
                    byte[] block = cacheBlock.getBlock(uuid);
                    String getResult = null;
                    try {
                        if (block != null) {
                            int nonZeroIndexLast = 0;
                            for (int j = block.length - 1; j >= 0; j--) {
                                if (block[j] != 0) {
                                    nonZeroIndexLast = j;
                                    break;
                                }
                            }
                            getResult = new String(ArrayUtils.subarray(block, 0, nonZeroIndexLast), UTF_8);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    logger.error(uuid + " Get-Test-" + ci + " : getResult = " + getResult);
                }
            });
            getThreadList.add(thread);
        }
        for (int i = 0; i < N; i++) {
            setThreadList.get(i).start();
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getThreadList.get(i).start();
        }
    }
}
