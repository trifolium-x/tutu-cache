package co.tunan.tucache.core.config;

import co.tunan.tucache.core.util.SystemInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * tu-cache profiles configuration
 *
 * @author wangxudong
 * @date 2020/08/28
 */
@Getter
@Setter
@ToString
public class TuCacheProfiles {

    /**
     * 缓存的统一key前缀，默认为 ""
     */
    private String cachePrefix = "";

    /**
     * 线程池
     */
    private ThreadPool pool = new ThreadPool();

    /**
     * thread pool config
     */
    @Getter
    @Setter
    public static class ThreadPool {

        /*
         * 如果没有注入线程池
         * 默认线程池配置，核心线程为CPU核心数，最大线程为CPU核心*4
         * keepAliveTime 10秒，线程空闲时间超过10秒则关闭，保留核心线程
         * 队列长度默认为 Integer.MAX_VALUE
         */

        /**
         * defaults to core number
         * <p>
         * Ready-to-use threads prepared by default in the silent state.
         * </p>
         */
        private int coreThreadNum = SystemInfo.MACHINE_CORE_NUM;

        /**
         * The maximum number of threads defaults to min number * 4
         * the minimum value is coreThreadNum
         */
        private int maxThreadNum = SystemInfo.MACHINE_CORE_NUM * 4;

        private long keepAliveTime = 10000L;
    }
}
