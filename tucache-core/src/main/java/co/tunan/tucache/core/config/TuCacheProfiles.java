package co.tunan.tucache.core.config;

import co.tunan.tucache.core.util.SystemInfo;

/**
 * tu-cache profiles configuration
 *
 * @author wangxudong
 * @date 2020/08/28
 */
public class TuCacheProfiles {

    /**
     * 缓存的统一key前缀，默认为 ""
     */
    private String cachePrefix = "";

    /**
     * 线程池
     */
    private ThreadPool pool = new ThreadPool();

    public String getCachePrefix() {
        return cachePrefix;
    }

    public void setCachePrefix(String cachePrefix) {
        this.cachePrefix = cachePrefix;
    }

    public ThreadPool getPool() {
        return pool;
    }

    public void setPool(ThreadPool pool) {
        this.pool = pool;
    }

    /**
     * thread pool config
     */
    public static class ThreadPool {

        /*
         * 如果没有注入线程池
         * 默认线程池配置，核心线程为CPU核心数，最大线程为CPU核心*4
         * keepAliveTime 10秒，线程空闲时间超过10秒则关闭，保留核心线程
         * 队列长度为 Integer.MAX_VALUE
         */

        private int corePoolSize = SystemInfo.MACHINE_CORE_NUM;

        private int maximumPoolSize = SystemInfo.MACHINE_CORE_NUM * 4;

        private int maxQueueSize = Integer.MAX_VALUE;

        private long keepAliveTime = 10000L;

        public int getMaximumPoolSize() {
            return maximumPoolSize;
        }

        public void setMaximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
        }

        public int getMaxQueueSize() {
            return maxQueueSize;
        }

        public void setMaxQueueSize(int maxQueueSize) {
            this.maxQueueSize = maxQueueSize;
        }

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public long getKeepAliveTime() {
            return keepAliveTime;
        }

        public void setKeepAliveTime(long keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
        }

    }

    @Override
    public String toString() {

        return "cachePrefix:" + cachePrefix;
    }

}
